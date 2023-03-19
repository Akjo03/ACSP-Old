package com.akjostudios.acsp.backend.service.oauth2;

import com.akjostudios.acsp.backend.dto.oauth2.discord.DiscordLoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class LoginService {
	private final WebClient discordClient;
	@Value("${application.oauth2.discord.client-id}")
	private String discordClientId;
	@Value("${application.oauth2.discord.client-secret}")
	private String discordClientSecret;
	@Value("${application.oauth2.discord.redirect-uri}")
	private String discordRedirectUri;

	public LoginService(WebClient.Builder webClientBuilder) {
		this.discordClient = webClientBuilder.baseUrl("https://discord.com/api").build();
	}

	public DiscordLoginResponse loginDiscord(String code, List<String> scope) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("client_id", discordClientId);
		formData.add("client_secret", discordClientSecret);
		formData.add("grant_type", "authorization_code");
		formData.add("code", code);
		formData.add("redirect_uri", discordRedirectUri);
		formData.add("scope", String.join(" ", scope));

		return discordClient.post()
				.uri("/oauth2/token")
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.body(BodyInserters.fromFormData(formData))
				.exchangeToMono(response -> {
					if (response.statusCode().isError()) {
						return response.bodyToMono(String.class).flatMap(body -> Mono.error(new RuntimeException(body)));
					} else {
						return response.bodyToMono(DiscordLoginResponse.class);
					}
				}).block();
	}
}