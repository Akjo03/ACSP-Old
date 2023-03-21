package com.akjostudios.acsp.backend.controller.oauth2;

import com.akjostudios.acsp.backend.dto.oauth2.discord.DiscordAuthResponse;
import com.akjostudios.acsp.backend.service.oauth2.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/oauth2/login")
@RequiredArgsConstructor
public class LoginController {
	private final LoginService LoginService;

	@GetMapping("/discord")
	public ResponseEntity<String> loginDiscord(String code) {
		DiscordAuthResponse authResponse = LoginService.loginDiscord(code, List.of(
				"identify",
				"email",
				"guilds"
		));
		return ResponseEntity.ok("Logged in successfully!");
	}
}