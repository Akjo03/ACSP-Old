package com.akjostudios.acsp.backend.controller.user;

import com.akjostudios.acsp.backend.dto.user.UserDto;
import com.akjostudios.acsp.backend.services.user.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
	private final UserSessionService userSessionService;

	@GetMapping("/@me")
	@PreAuthorize("hasAuthority('ME_USER:READ')")
	public ResponseEntity<UserDto> getMe(@RequestHeader("X-Session-ID") String sessionId) {
		UserDto userDto = userSessionService.getUserBySessionId(sessionId);
		if (userDto == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(userDto);
	}
}