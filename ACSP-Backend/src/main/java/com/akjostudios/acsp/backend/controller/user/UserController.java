package com.akjostudios.acsp.backend.controller.user;

import com.akjostudios.acsp.backend.data.dto.user.UserDto;
import com.akjostudios.acsp.backend.services.user.UserSessionService;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
	private static final Logger LOGGER = LoggerManager.getLogger(UserController.class);

	private final UserSessionService userSessionService;

	@GetMapping("/@me")
	@PreAuthorize("hasAuthority('ME_USER:READ')")
	public ResponseEntity<UserDto> getMe(@CookieValue("session_id") String sessionId) {
		return ResponseEntity.ok(userSessionService.getUserBySessionId(sessionId));
	}
}