package com.akjostudios.acsp.backend.controller.user;

import com.akjostudios.acsp.backend.config.auth.AcspSecretConfiguration;
import com.akjostudios.acsp.backend.data.model.AcspUserSession;
import com.akjostudios.acsp.backend.data.model.AcspUserSessionStatus;
import com.akjostudios.acsp.backend.services.auth.BeginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/onboarding")
public class OnboardingController {
	private final AcspSecretConfiguration acspSecretConfiguration;

	private final BeginService beginService;

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
}