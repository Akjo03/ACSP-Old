package com.akjostudios.acsp.backend.controller.auth;

import com.akjostudios.acsp.backend.dto.BeginAuthResponseDto;
import com.akjostudios.acsp.backend.model.BeginRequest;
import com.akjostudios.acsp.backend.repository.BeginRequestRepository;
import com.akjostudios.acsp.backend.services.SecurityService;
import com.akjostudios.acsp.backend.services.auth.BeginService;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/begin")
public class BeginController {
	private static final Logger LOGGER = LoggerManager.getLogger(BeginController.class);

	private final BeginService beginService;
	private final SecurityService securityService;
	private final BeginRequestRepository beginRequestRepository;

	@Value("${application.secrets.acsp-begin-secret}")
	private String acspBeginSecret;

	@GetMapping("")
	public ResponseEntity<BeginAuthResponseDto> beginAuth(String userId, String secret) {
		if (secret == null || !secret.equals(acspBeginSecret)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
		beginRequestRepository.save(newBeginRequest);

		return ResponseEntity.ok(beginService.getBeginAuthReponseDto(newBeginRequest));
	}



	@GetMapping("/authenticate")
	public ResponseEntity<String> authenticate(String userId, String code) {
		return ResponseEntity.ok(beginService.makeUrlUnsafe(code));
	}
}