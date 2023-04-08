package com.akjostudios.acsp.backend.controller.user;

import com.akjostudios.acsp.backend.constants.CookieConstants;
import com.akjostudios.acsp.backend.data.dto.user.UserSessionStatusDto;
import com.akjostudios.acsp.backend.services.user.UserSessionService;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserSessionController {
	private static final Logger LOGGER = LoggerManager.getLogger(UserSessionController.class);

	private final UserSessionService userSessionService;

	@GetMapping("/session/status")
	public ResponseEntity<UserSessionStatusDto> getUserSessionStatus(
			@RequestParam(required = false) String userId,
			@CookieValue(value = CookieConstants.USER_ID, required = false) String cookieUserId
	) {
		String theUserId = userId == null ? (cookieUserId == null ? "" : cookieUserId) : userId;
		UserSessionStatusDto userSessionStatusDto = userSessionService.getUserSessionStatus(theUserId);
		if (userSessionStatusDto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(userSessionStatusDto);
	}
}