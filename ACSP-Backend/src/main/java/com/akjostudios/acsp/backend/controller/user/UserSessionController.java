package com.akjostudios.acsp.backend.controller.user;

import com.akjostudios.acsp.backend.dto.user.UserSessionRefreshDto;
import com.akjostudios.acsp.backend.dto.user.UserSessionStatusDto;
import com.akjostudios.acsp.backend.services.user.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserSessionController {
	private final UserSessionService userSessionService;

	@GetMapping("/{userId}/session/status")
	@PreAuthorize("hasRole('ROLE_BOT')")
	public ResponseEntity<UserSessionStatusDto> getUserSessionStatus(@PathVariable String userId) {
		UserSessionStatusDto userSessionStatusDto = userSessionService.getUserSessionStatus(userId);
		if (userSessionStatusDto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(userSessionStatusDto);
	}
}