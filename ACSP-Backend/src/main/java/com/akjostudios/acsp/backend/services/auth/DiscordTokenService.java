package com.akjostudios.acsp.backend.services.auth;

import com.akjostudios.acsp.backend.dto.auth.DiscordAuthTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscordTokenService {
	@Value("${application.oauth2.discord.client-id}")
	private String clientId;

	@Value("${application.oauth2.discord.client-secret}")
	private String clientSecret;

	@Value("${application.oauth2.discord.redirect-uri}")
	private String redirectUri;

	public DiscordAuthTokenRequest getDiscordAuthTokenRequest(String code) {
		DiscordAuthTokenRequest discordAuthTokenRequest = new DiscordAuthTokenRequest();

		discordAuthTokenRequest.setClientId(clientId);
		discordAuthTokenRequest.setClientSecret(clientSecret);
		discordAuthTokenRequest.setCode(code);
		discordAuthTokenRequest.setGrantType("authorization_code");
		discordAuthTokenRequest.setRedirectUri(redirectUri);

		return discordAuthTokenRequest;
	}
}