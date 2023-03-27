package com.akjostudios.acsp.backend.controller.user;

import com.akjostudios.acsp.backend.dto.user.UserDto;
import com.akjostudios.acsp.backend.model.AcspUser;
import com.akjostudios.acsp.backend.model.AcspUserSession;
import com.akjostudios.acsp.backend.repository.UserRepository;
import com.akjostudios.acsp.backend.repository.UserSessionRepository;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
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
	private final UserSessionRepository userSessionRepository;
	private final UserRepository userRepository;

	@GetMapping("/@me")
	@PreAuthorize("hasAuthority('ME_USER:READ')")
	public ResponseEntity<UserDto> getMe(@RequestHeader("X-Session-ID") String sessionId) {
		AcspUserSession session = userSessionRepository.findBySessionId(sessionId);
		if (session == null) {
			return ResponseEntity.notFound().build();
		}

		AcspUser user = userRepository.findByUserId(session.getUserId());
		if (user == null) {
			return ResponseEntity.notFound().build();
		}

		UserDto userDto = new UserDto();
		userDto.setUser(user);
		return ResponseEntity.ok(userDto);
	}
}