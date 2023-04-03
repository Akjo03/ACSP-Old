package com.akjostudios.acsp.backend.services.auth;

import com.akjostudios.acsp.backend.data.dto.auth.DiscordAuthCodeRequest;
import com.akjostudios.acsp.backend.data.dto.auth.DiscordAuthTokenRequest;
import com.akjostudios.acsp.backend.data.dto.auth.DiscordAuthTokenResponse;
import com.akjostudios.acsp.backend.data.dto.discord.DiscordUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DiscordAuthService {
	@Value("${application.oauth2.discord.client-id}")
	private String clientId;

	@Value("${application.oauth2.discord.client-secret}")
	private String clientSecret;

	@Qualifier("discordTokenClient")
	private final WebClient discordTokenClient;
	@Qualifier("discordApiClient")
	private final WebClient discordApiClient;

	public DiscordAuthCodeRequest getDiscordAuthCodeRequest(String code, String redirectUri) {
		DiscordAuthCodeRequest discordAuthCodeRequest = new DiscordAuthCodeRequest();
		discordAuthCodeRequest.setClientId(clientId);
		discordAuthCodeRequest.setRedirectUri(redirectUri);
		discordAuthCodeRequest.setScope(Stream.of(
				"identify", "email", "guilds"
		).reduce((s1, s2) -> s1 + "%20" + s2).orElse(""));
		discordAuthCodeRequest.setState(code);
		return discordAuthCodeRequest;
	}

	public DiscordAuthTokenResponse getDiscordAuthTokenResponse(String code, String redirectUri) {
		DiscordAuthTokenRequest discordAuthTokenRequest = getDiscordAuthTokenRequest(code, redirectUri);

		DiscordAuthTokenResponse discordAuthTokenResponse;
		try {
			discordAuthTokenResponse = discordTokenClient.post()
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
					.body(BodyInserters.fromFormData(discordAuthTokenRequest.toMultiValueMap()))
					.retrieve()
					.bodyToMono(DiscordAuthTokenResponse.class)
					.block();
		} catch (Exception e) {
			return null;
		}

		return discordAuthTokenResponse;
	}

	public DiscordUserResponse getDiscordUserResponse(DiscordAuthTokenResponse discordAuthTokenResponse) {
		Optional<DiscordUserResponse> discordUserResponse;
		try {
			discordUserResponse = discordApiClient.get()
					.uri("/users/@me")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + discordAuthTokenResponse.getAccessToken())
					.retrieve()
					.bodyToMono(DiscordUserResponse.class)
					.blockOptional();
		} catch (Exception e) {
			return null;
		}
		if (discordUserResponse.isEmpty()) {
			return null;
		}
		DiscordUserResponse discordUser = discordUserResponse.get();
		if (discordUser.getId() == null || discordUser.getUsername() == null || discordUser.getDiscriminator() == null) {
			return null;
		}
		return discordUser;
	}

	private DiscordAuthTokenRequest getDiscordAuthTokenRequest(String code, String redirectUri) {
		DiscordAuthTokenRequest discordAuthTokenRequest = new DiscordAuthTokenRequest();

		discordAuthTokenRequest.setClientId(clientId);
		discordAuthTokenRequest.setClientSecret(clientSecret);
		discordAuthTokenRequest.setCode(code);
		discordAuthTokenRequest.setGrantType("authorization_code");
		discordAuthTokenRequest.setRedirectUri(redirectUri);

		return discordAuthTokenRequest;
	}
}