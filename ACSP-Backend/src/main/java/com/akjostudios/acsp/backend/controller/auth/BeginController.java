package com.akjostudios.acsp.backend.controller.auth;

import com.akjostudios.acsp.backend.config.auth.AcspSecretConfiguration;
import com.akjostudios.acsp.backend.data.dto.auth.BeginLinkResponseDto;
import com.akjostudios.acsp.backend.data.dto.auth.DiscordAuthCodeRequest;
import com.akjostudios.acsp.backend.data.dto.auth.DiscordAuthTokenResponse;
import com.akjostudios.acsp.backend.data.dto.discord.DiscordUserResponse;
import com.akjostudios.acsp.backend.data.model.AcspBeginRequest;
import com.akjostudios.acsp.backend.data.model.AcspUser;
import com.akjostudios.acsp.backend.data.model.AcspUserSession;
import com.akjostudios.acsp.backend.data.repository.BeginRequestRepository;
import com.akjostudios.acsp.backend.data.repository.UserRepository;
import com.akjostudios.acsp.backend.data.repository.UserSessionRepository;
import com.akjostudios.acsp.backend.services.auth.BeginService;
import com.akjostudios.acsp.backend.services.auth.DiscordAuthService;
import com.akjostudios.acsp.backend.services.security.SecurityService;
import com.akjostudios.acsp.backend.util.RedirectUtils;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/begin")
public class BeginController {
	private static final Logger LOGGER = LoggerManager.getLogger(BeginController.class);

	private final Lock createUserLock = new ReentrantLock();

	private final AcspSecretConfiguration acspSecretConfiguration;

	private final BeginService beginService;
	private final SecurityService securityService;
	private final DiscordAuthService discordAuthService;

	private final BeginRequestRepository beginRequestRepository;
	private final UserRepository userRepository;
	private final UserSessionRepository userSessionRepository;

	@Qualifier("discordBotClient")
	private final WebClient discordBotClient;

	@Value("${application.oauth2.discord.begin-redirect-uri}")
	private String beginRedirectUri;

	@GetMapping("")
	public ResponseEntity<BeginLinkResponseDto> beginAuth(@RequestParam String userId, @RequestParam String secret, @RequestParam String messageId) {
		if (userId == null || userId.isBlank() || messageId == null || messageId.isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		if (secret == null || secret.isBlank() || !secret.equals(acspSecretConfiguration.getAcspBeginSecret())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		discordBotClient.delete()
				.uri("/begin?messageId=" +  messageId)
				.retrieve()
				.bodyToMono(Void.class)
				.block();

		AcspUser acspUser = userRepository.findByUserId(userId);
		AcspUserSession acspUserSession = userSessionRepository.findByUserId(userId);
		ResponseEntity<BeginLinkResponseDto> existingUserResponse = beginService.getExistingUserBeginLinkResponse(acspUser, acspUserSession);
		if (existingUserResponse != null) {
			return existingUserResponse;
		}

		AcspBeginRequest beginRequest = beginRequestRepository.findByUserId(userId);
		if (beginRequest != null) {
			return ResponseEntity.ok(beginService.getBeginAuthLinkReponseDto(beginRequest));
		}

		String salt = securityService.generateSalt();
		SecretKey secretKey;
		try {
			secretKey = securityService.getKeyFromPassword(secret, salt);
		} catch (Exception e) {
			LOGGER.error("Error while generating secret key", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		byte[] encUserId = Base64.getEncoder().encode(userId.getBytes());
		String code;
		try {
			code = securityService.encrypt(new String(encUserId), secretKey, securityService.generateIv());
		} catch (Exception e) {
			LOGGER.error("Error while encrypting user id", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		AcspBeginRequest newBeginRequest = beginService.createBeginRequest(userId, code, salt);
		beginRequestRepository.save(newBeginRequest);

		return ResponseEntity.ok(beginService.getBeginAuthLinkReponseDto(newBeginRequest));
	}

	@GetMapping("/authenticate")
	public ResponseEntity<String> authenticate(@RequestParam String userId, @RequestParam String code) {
		if (userId == null || userId.isBlank() || code == null || code.isBlank()) {
			return ResponseEntity.badRequest().build();
		}

		AcspUser acspUser = userRepository.findByUserId(userId);
		AcspUserSession acspUserSession = userSessionRepository.findByUserId(userId);
		ResponseEntity<BeginLinkResponseDto> existingUserResponse = beginService.getExistingUserBeginLinkResponse(acspUser, acspUserSession);
		if (existingUserResponse != null) {
			String alreadyAuthenticatedMessage = beginService.getAlreadyAuthenticatedMessage(existingUserResponse);
			return ResponseEntity.ok(alreadyAuthenticatedMessage);
		}

		AcspBeginRequest beginRequest = beginRequestRepository.findByUserId(userId);
		if (beginRequest == null) {
			return ResponseEntity.ok(beginService.getMissingBeginRequestMessage());
		}

		String realCode = beginService.makeUrlUnsafe(code);
		if (!realCode.equals(beginRequest.getCode())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		DiscordAuthCodeRequest discordAuthCodeRequest = discordAuthService.getDiscordAuthCodeRequest(code, beginRedirectUri);
		if (discordAuthCodeRequest == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return RedirectUtils.getRedirectResponse(discordAuthCodeRequest.toUrl());
	}

	@GetMapping("/code")
	@SuppressWarnings("DuplicatedCode")
	public ResponseEntity<String> code(@RequestParam String code, @RequestParam String state) {
		if (code == null || code.isBlank() || state == null || state.isBlank()) {
			return ResponseEntity.badRequest().build();
		}

		String realState = beginService.makeUrlUnsafe(state);
		AcspBeginRequest beginRequest = beginRequestRepository.findByCode(realState);
		if (beginRequest == null) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(beginService.getInvalidStateMessage());
		}

		AcspUserSession acspUserSession = userSessionRepository.findByUserId(beginRequest.getUserId());
		ResponseEntity<String> existingUserResponse = beginService.getExistingUserAndSessionResponse(acspUserSession);
		if (existingUserResponse != null) {
			return existingUserResponse;
		}

		DiscordAuthTokenResponse discordAuthTokenResponse = discordAuthService.getDiscordAuthTokenResponse(code, beginRedirectUri);
		if (discordAuthTokenResponse == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		DiscordUserResponse discordUserResponse = discordAuthService.getDiscordUserResponse(discordAuthTokenResponse);
		if (discordUserResponse == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		createUserLock.lock();
		try {
			AcspUser acspUser = userRepository.findByUserId(beginRequest.getUserId());
			acspUserSession = userSessionRepository.findByUserId(beginRequest.getUserId());

			beginRequestRepository.delete(beginRequest);
			return beginService.getOnboardingResponse(acspUser, acspUserSession, discordUserResponse, discordAuthTokenResponse);
		} finally {
			createUserLock.unlock();
		}
	}
}