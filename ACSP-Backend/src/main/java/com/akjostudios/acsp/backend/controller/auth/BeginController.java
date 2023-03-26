package com.akjostudios.acsp.backend.controller.auth;

import com.akjostudios.acsp.backend.config.auth.AcspSecretConfiguration;
import com.akjostudios.acsp.backend.dto.auth.BeginLinkResponseDto;
import com.akjostudios.acsp.backend.dto.auth.DiscordAuthCodeRequest;
import com.akjostudios.acsp.backend.dto.auth.DiscordAuthTokenResponse;
import com.akjostudios.acsp.backend.dto.discord.DiscordUserResponse;
import com.akjostudios.acsp.backend.model.AcspUser;
import com.akjostudios.acsp.backend.model.AcspUserSession;
import com.akjostudios.acsp.backend.model.BeginRequest;
import com.akjostudios.acsp.backend.repository.BeginRequestRepository;
import com.akjostudios.acsp.backend.repository.UserRepository;
import com.akjostudios.acsp.backend.repository.UserSessionRepository;
import com.akjostudios.acsp.backend.services.SecurityService;
import com.akjostudios.acsp.backend.services.auth.BeginService;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.SecretKey;
import java.net.URI;
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

	private final BeginRequestRepository beginRequestRepository;
	private final UserRepository userRepository;
	private final UserSessionRepository userSessionRepository;

	@Qualifier("discordBotClient")
	private final WebClient discordBotClient;

	@GetMapping("")
	public ResponseEntity<BeginLinkResponseDto> beginAuth(@RequestParam String userId, @RequestParam String secret, @RequestParam String messageId) {
		// Check if the arguments are valid
		if (userId == null || userId.isBlank() || messageId == null || messageId.isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		// Check if the secret is correct
		if (secret == null || secret.isBlank() || !secret.equals(acspSecretConfiguration.getAcspBeginSecret())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// Delete the original !begin message
		discordBotClient.delete()
				.uri("/begin?messageId=" +  messageId)
				.retrieve()
				.bodyToMono(Void.class)
				.block();

		// Respond to existing user and/or session
		AcspUser acspUser = userRepository.findByUserId(userId);
		AcspUserSession acspUserSession = userSessionRepository.findByUserId(userId);
		ResponseEntity<BeginLinkResponseDto> existingUserResponse = beginService.getExistingUserBeginLinkResponse(acspUser, acspUserSession);
		if (existingUserResponse != null) {
			return existingUserResponse;
		}

		// Respond to existing begin request
		BeginRequest beginRequest = beginRequestRepository.findByUserId(userId);
		if (beginRequest != null) {
			return ResponseEntity.ok(beginService.getBeginAuthLinkReponseDto(beginRequest));
		}

		// Create new secret key using the secret
		String salt = securityService.generateSalt();
		SecretKey secretKey;
		try {
			secretKey = securityService.getKeyFromPassword(secret, salt);
		} catch (Exception e) {
			LOGGER.error("Error while generating secret key", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// Encrypt the userId using the secret key
		byte[] encUserId = Base64.getEncoder().encode(userId.getBytes());
		String code;
		try {
			code = securityService.encrypt(new String(encUserId), secretKey, securityService.generateIv());
		} catch (Exception e) {
			LOGGER.error("Error while encrypting user id", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Create new begin request
		BeginRequest newBeginRequest = beginService.createBeginRequest(userId, code, salt);
		beginRequestRepository.save(newBeginRequest);

		// Respond with the begin link
		return ResponseEntity.ok(beginService.getBeginAuthLinkReponseDto(newBeginRequest));
	}

	@GetMapping("/authenticate")
	public ResponseEntity<String> authenticate(@RequestParam String userId, @RequestParam String code) {
		if (userId == null || userId.isBlank() || code == null || code.isBlank()) {
			return ResponseEntity.badRequest().build();
		}

		// Respond to existing user and/or session
		AcspUser acspUser = userRepository.findByUserId(userId);
		AcspUserSession acspUserSession = userSessionRepository.findByUserId(userId);
		ResponseEntity<BeginLinkResponseDto> existingUserResponse = beginService.getExistingUserBeginLinkResponse(acspUser, acspUserSession);
		if (existingUserResponse != null) {
			String alreadyAuthenticatedMessage = beginService.getAlreadyAuthenticatedMessage(existingUserResponse);
			return ResponseEntity.ok(alreadyAuthenticatedMessage);
		}

		// Respond to non-existing begin request
		BeginRequest beginRequest = beginRequestRepository.findByUserId(userId);
		if (beginRequest == null) {
			return ResponseEntity.ok(beginService.getMissingBeginRequestMessage());
		}

		// Respond to invalid code
		String realCode = beginService.makeUrlUnsafe(code);
		if (!realCode.equals(beginRequest.getCode())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// Get the discord auth url
		DiscordAuthCodeRequest discordAuthCodeRequest = beginService.getDiscordAuthCodeRequest(code);
		if (discordAuthCodeRequest == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		String discordAuthUrl = discordAuthCodeRequest.toUrl();

		// Redirect to the discord auth url
		HttpHeaders redirectHeaders = new HttpHeaders();
		redirectHeaders.setLocation(URI.create(discordAuthUrl));
		return ResponseEntity.status(HttpStatus.SEE_OTHER).headers(redirectHeaders).build();
	}

	@GetMapping("/code")
	public ResponseEntity<String> code(@RequestParam String code, @RequestParam String state) {
		if (code == null || code.isBlank() || state == null || state.isBlank()) {
			return ResponseEntity.badRequest().build();
		}

		// Respond to invalid state
		String realState = beginService.makeUrlUnsafe(state);
		BeginRequest beginRequest = beginRequestRepository.findByCode(realState);
		if (beginRequest == null) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(beginService.getInvalidStateMessage());
		}

		// Respond to existing user and session
		AcspUser acspUser = userRepository.findByUserId(beginRequest.getUserId());
		AcspUserSession acspUserSession = userSessionRepository.findByUserId(beginRequest.getUserId());
		ResponseEntity<String> existingUserResponse = beginService.getExistingUserAndSessionResponse(acspUserSession);
		if (existingUserResponse != null) {
			return existingUserResponse;
		}

		// Get discord auth token
		DiscordAuthTokenResponse discordAuthTokenResponse = beginService.getDiscordAuthTokenResponse(code);
		if (discordAuthTokenResponse == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Get discord user
		DiscordUserResponse discordUserResponse = beginService.getDiscordUserResponse(discordAuthTokenResponse);
		if (discordUserResponse == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// Create user and session
		createUserLock.lock();
		try {
			acspUser = userRepository.findByUserId(beginRequest.getUserId());
			acspUserSession = userSessionRepository.findByUserId(beginRequest.getUserId());

			beginRequestRepository.delete(beginRequest);
			return beginService.getOnboardingResponse(acspUser, acspUserSession, discordUserResponse, discordAuthTokenResponse);
		} finally {
			createUserLock.unlock();
		}
	}
}