package com.akjostudios.acsp.backend.controller.user;

import com.akjostudios.acsp.backend.constants.CookieConstants;
import com.akjostudios.acsp.backend.data.dto.user.UserSessionStatusDto;
import com.akjostudios.acsp.backend.services.user.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserSessionController {
	private final UserSessionService userSessionService;

	@GetMapping("/session/status")
	public ResponseEntity<UserSessionStatusDto> getUserSessionStatus(@CookieValue(CookieConstants.USER_ID) String userId) {
		UserSessionStatusDto userSessionStatusDto = userSessionService.getUserSessionStatus(userId);
		if (userSessionStatusDto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(userSessionStatusDto);
	}
}