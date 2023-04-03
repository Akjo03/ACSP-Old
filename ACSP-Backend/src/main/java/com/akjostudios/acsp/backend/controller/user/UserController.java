package com.akjostudios.acsp.backend.controller.user;

import com.akjostudios.acsp.backend.data.dto.user.UserDto;
import com.akjostudios.acsp.backend.data.model.AcspUser;
import com.akjostudios.acsp.backend.services.discord.DiscordContentService;
import com.akjostudios.acsp.backend.services.user.UserSessionService;
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
	private final UserSessionService userSessionService;
	private final DiscordContentService discordContentService;

	@GetMapping("/@me")
	@PreAuthorize("hasAuthority('ME_USER:READ')")
	public ResponseEntity<UserDto> getMe(@CookieValue("session_id") String sessionId) {
		UserDto userDto = userSessionService.getUserBySessionId(sessionId);
		return ResponseEntity.ok(userDto);
	}

	@GetMapping("/@me/avatar")
	@PreAuthorize("hasAuthority('ME_USER:READ')")
	public ResponseEntity<String> getMeAvatarUrl(@CookieValue("session_id") String sessionId) {
		AcspUser user = userSessionService.getUserBySessionId(sessionId).getUser();

		String userId = user.getUserId();
		String avatarHash = user.getAvatar();

		String avatarUrl = discordContentService.getAvatarUrl(userId, avatarHash);
		return ResponseEntity.ok(avatarUrl);
	}
}