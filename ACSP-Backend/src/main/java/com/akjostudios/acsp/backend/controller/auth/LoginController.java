package com.akjostudios.acsp.backend.controller.auth;

import com.akjostudios.acsp.backend.config.ApplicationConfig;
import com.akjostudios.acsp.backend.data.dto.auth.DiscordAuthCodeRequest;
import com.akjostudios.acsp.backend.data.dto.auth.DiscordAuthTokenResponse;
import com.akjostudios.acsp.backend.data.dto.discord.DiscordUserResponse;
import com.akjostudios.acsp.backend.data.model.AcspLoginRequest;
import com.akjostudios.acsp.backend.data.model.AcspUser;
import com.akjostudios.acsp.backend.data.model.AcspUserSession;
import com.akjostudios.acsp.backend.data.model.AcspUserSessionStatus;
import com.akjostudios.acsp.backend.data.repository.LoginRequestRepository;
import com.akjostudios.acsp.backend.data.repository.UserRepository;
import com.akjostudios.acsp.backend.data.repository.UserSessionRepository;
import com.akjostudios.acsp.backend.services.auth.BeginService;
import com.akjostudios.acsp.backend.services.auth.DiscordAuthService;
import com.akjostudios.acsp.backend.services.auth.LoginService;
import com.akjostudios.acsp.backend.util.RandomStrings;
import com.akjostudios.acsp.backend.util.RedirectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/login")
public class LoginController {
	private final DiscordAuthService discordAuthService;

	private final BeginService beginService;
	private final LoginService loginService;
	private final LoginRequestRepository loginRequestRepository;

	private final UserRepository userRepository;
	private final UserSessionRepository userSessionRepository;

	private final ApplicationConfig applicationConfig;

	@Value("${application.oauth2.discord.login-redirect-uri}")
	private String loginRedirectUri;

	@GetMapping("")
	public ResponseEntity<String> login() {
		String code = RandomStrings.generate(24);
		DiscordAuthCodeRequest discordAuthCodeRequest = discordAuthService.getDiscordAuthCodeRequest(code, loginRedirectUri);
		if (discordAuthCodeRequest == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		AcspLoginRequest acspLoginRequest = new AcspLoginRequest();
		acspLoginRequest.setCode(code);
		loginRequestRepository.save(acspLoginRequest);

		return RedirectUtils.getRedirectResponse(discordAuthCodeRequest.toUrl());
	}

	@GetMapping("/code")
	@SuppressWarnings("DuplicatedCode")
	public ResponseEntity<String> code(@RequestParam String code, @RequestParam String state) {
		if (code == null || code.isBlank() || state == null || state.isBlank()) {
			return ResponseEntity.badRequest().build();
		}

		AcspLoginRequest acspLoginRequest = loginRequestRepository.findByCode(state);
		if (acspLoginRequest == null) {
			return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(loginService.getInvalidStateMessage());
		}

		DiscordAuthTokenResponse discordAuthTokenResponse = discordAuthService.getDiscordAuthTokenResponse(code, loginRedirectUri);
		if (discordAuthTokenResponse == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		DiscordUserResponse discordUserResponse = discordAuthService.getDiscordUserResponse(discordAuthTokenResponse);
		if (discordUserResponse == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		AcspUserSession acspUserSession = userSessionRepository.findByUserId(discordUserResponse.getId());
		if (acspUserSession == null) {
			loginRequestRepository.delete(acspLoginRequest);
			return RedirectUtils.getRedirectResponse(applicationConfig.getDiscordServerUrl());
		}
		AcspUser acspUser = userRepository.findByUserId(discordUserResponse.getId());
		if (acspUser == null) {
			loginRequestRepository.delete(acspLoginRequest);
			return RedirectUtils.getRedirectResponse(applicationConfig.getDiscordServerUrl());
		}

		acspUserSession.setSessionToken(discordAuthTokenResponse.getAccessToken());
		acspUserSession.setRefreshToken(discordAuthTokenResponse.getRefreshToken());
		userSessionRepository.save(acspUserSession);

		loginRequestRepository.delete(acspLoginRequest);

		return switch (AcspUserSessionStatus.fromString(acspUserSession.getStatus())) {
			case ONBOARDING -> beginService.getOnboardingResponse(acspUser, acspUserSession, discordUserResponse, discordAuthTokenResponse);
			case ACTIVE -> loginService.getActiveSessionResponse();
			case INACTIVE -> loginService.getLoginSuccessResponse();
		};
	}
}