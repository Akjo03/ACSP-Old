package com.akjostudios.acsp.backend.controller.auth;

import com.akjostudios.acsp.backend.config.SecurityConfig;
import com.akjostudios.acsp.backend.dto.auth.BeginAuthResponseDto;
import com.akjostudios.acsp.backend.dto.auth.DiscordAuthCodeRequest;
import com.akjostudios.acsp.backend.dto.auth.DiscordAuthTokenRequest;
import com.akjostudios.acsp.backend.dto.auth.DiscordAuthTokenResponse;
import com.akjostudios.acsp.backend.dto.discord.DiscordUserResponse;
import com.akjostudios.acsp.backend.model.AcspUser;
import com.akjostudios.acsp.backend.model.BeginRequest;
import com.akjostudios.acsp.backend.repository.BeginRequestRepository;
import com.akjostudios.acsp.backend.repository.UserRepository;
import com.akjostudios.acsp.backend.services.SecurityService;
import com.akjostudios.acsp.backend.services.auth.BeginService;
import com.akjostudios.acsp.backend.services.auth.DiscordTokenService;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.SecretKey;
import java.net.URI;
import java.util.Base64;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/begin")
public class BeginController {
	private static final Logger LOGGER = LoggerManager.getLogger(BeginController.class);

	private final BeginService beginService;
	private final SecurityService securityService;
	private final DiscordTokenService discordTokenService;

	private final BeginRequestRepository beginRequestRepository;
	private final UserRepository userRepository;

	private final SecurityConfig securityConfig;

	@Qualifier("discordTokenClient")
	private final WebClient discordTokenClient;

	@Qualifier("discordApiClient")
	private final WebClient discordApiClient;

	@Value("${application.secrets.acsp-begin-secret}")
	private String acspBeginSecret;

	@GetMapping("")
	public ResponseEntity<BeginAuthResponseDto> beginAuth(String userId, String secret) {
		if (secret == null || !secret.equals(acspBeginSecret)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		AcspUser acspUser = userRepository.findByUserId(userId);
		if (acspUser != null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		BeginRequest beginRequest = beginRequestRepository.findByUserId(userId);
		if (beginRequest != null) {
			return ResponseEntity.ok(beginService.getBeginAuthReponseDto(beginRequest));
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

		BeginRequest newBeginRequest = new BeginRequest();
		newBeginRequest.setUserId(userId);
		newBeginRequest.setCode(code);
		newBeginRequest.setAuthState("begin");
		beginRequestRepository.save(newBeginRequest);

		return ResponseEntity.ok(beginService.getBeginAuthReponseDto(newBeginRequest));
	}

	@GetMapping("/authenticate")
	public ResponseEntity<String> authenticate(String userId, String code) {
		if (userId == null || code == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		AcspUser acspUser = userRepository.findByUserId(userId);
		if (acspUser != null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		BeginRequest beginRequest = beginRequestRepository.findByUserId(userId);
		if (beginRequest == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (!beginRequest.getAuthState().equals("begin")) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		String realCode = beginService.makeUrlUnsafe(code);
		if (!realCode.equals(beginRequest.getCode())) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		DiscordAuthCodeRequest discordAuthCodeRequest = beginService.getDiscordAuthCodeRequest(code);
		if (discordAuthCodeRequest == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		beginRequestRepository.save(beginRequest);

		String discordAuthUrl = discordAuthCodeRequest.toUrl();

		HttpHeaders redirectHeaders = new HttpHeaders();
		redirectHeaders.setLocation(URI.create(discordAuthUrl));
		return new ResponseEntity<>(redirectHeaders, HttpStatus.SEE_OTHER);
	}

	@GetMapping("/code")
	public ResponseEntity<String> code(String code, String state) {
		if (code == null || state == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		String realState = beginService.makeUrlUnsafe(state);
		BeginRequest beginRequest = beginRequestRepository.findByCode(realState);
		if (beginRequest == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		if (!beginRequest.getAuthState().equals("begin")) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		DiscordAuthTokenRequest discordAuthTokenRequest = discordTokenService.getDiscordAuthTokenRequest(code);
		if (discordAuthTokenRequest == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		DiscordAuthTokenResponse discordAuthTokenResponse;
		try {
			discordAuthTokenResponse = discordTokenClient.post()
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
					.body(BodyInserters.fromFormData(discordAuthTokenRequest.toMultiValueMap()))
					.retrieve()
					.bodyToMono(DiscordAuthTokenResponse.class)
					.onErrorMap(e -> {
						LOGGER.error("Error while getting Discord auth token", e);
						return e;
					}).block();
		} catch (Exception e) {
			LOGGER.error("Error while getting Discord auth token", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (discordAuthTokenResponse == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Optional<DiscordUserResponse> discordUserResponse;
		try {
			discordUserResponse = discordApiClient.get()
					.uri("/users/@me")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + discordAuthTokenResponse.getAccessToken())
					.retrieve()
					.bodyToMono(DiscordUserResponse.class)
					.onErrorMap(e -> {
						LOGGER.error("Error while getting Discord user", e);
						return e;
					}).blockOptional();
		} catch (Exception e) {
			LOGGER.error("Error while getting Discord user", e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (!discordUserResponse.isPresent()) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		DiscordUserResponse discordUser = discordUserResponse.get();
		if (discordUser.getId() == null || discordUser.getUsername() == null || discordUser.getDiscriminator() == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		AcspUser user = beginService.createUserFromUserResponse(discordUser, discordAuthTokenResponse.getAccessToken());
		userRepository.save(user);

		beginRequestRepository.delete(beginRequest);

		return ResponseEntity.ok("Success");
	}
}