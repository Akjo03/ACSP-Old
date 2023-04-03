package com.akjostudios.acsp.backend.controller.user;

import com.akjostudios.acsp.backend.config.ApplicationConfig;
import com.akjostudios.acsp.backend.config.auth.AcspSecretConfiguration;
import com.akjostudios.acsp.backend.data.model.AcspUser;
import com.akjostudios.acsp.backend.data.model.AcspUserSession;
import com.akjostudios.acsp.backend.data.model.AcspUserSessionStatus;
import com.akjostudios.acsp.backend.data.repository.UserRepository;
import com.akjostudios.acsp.backend.data.repository.UserSessionRepository;
import com.akjostudios.acsp.backend.services.auth.BeginService;
import com.akjostudios.acsp.backend.util.RedirectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/onboarding")
public class OnboardingController {
	private final ApplicationConfig applicationConfig;
	private final AcspSecretConfiguration acspSecretConfiguration;

	private final BeginService beginService;

	private final UserRepository userRepository;
	private final UserSessionRepository userSessionRepository;

	@GetMapping("")
	public ResponseEntity<String> redirectToOnboarding(@RequestParam String userId, @RequestParam String secret) {
		if (!acspSecretConfiguration.getAcspBeginSecret().equals(secret)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		AcspUserSession userSession = beginService.getUserSessionForUser(userId);
		if (userSession == null) {
			return ResponseEntity.notFound().build();
		}
		if (!userSession.getStatus().equals(AcspUserSessionStatus.ONBOARDING.getStatus())) {
			return ResponseEntity.badRequest().build();
		}

		return beginService.getOnboardingRedirectResponse(userSession);
	}

	@GetMapping("/abort")
	@PreAuthorize("hasAuthority('ME_USER:ONBOARDING:ABORT')")
	public ResponseEntity<String> abortOnboarding(@CookieValue("session_id") String sessionId) {
		AcspUserSession userSession = userSessionRepository.findBySessionId(sessionId);
		if (userSession == null) {
			return ResponseEntity.notFound().build();
		}
		if (!userSession.getStatus().equals(AcspUserSessionStatus.ONBOARDING.getStatus())) {
			return ResponseEntity.badRequest().build();
		}

		AcspUser user = userRepository.findByUserId(userSession.getUserId());
		if (user == null) {
			return ResponseEntity.notFound().build();
		}

		userSessionRepository.delete(userSession);
		userRepository.delete(user);

		return RedirectUtils.getRedirectResponse(applicationConfig.getAppBaseUrl());
	}
}