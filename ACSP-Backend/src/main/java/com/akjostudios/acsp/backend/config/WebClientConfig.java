package com.akjostudios.acsp.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
	private final WebClient.Builder webClientBuilder;

	@Bean
	@Qualifier("discordTokenClient")
	public WebClient discordTokenClient() {
		return webClientBuilder.baseUrl("https://discord.com/api/oauth2/token").build();
	}

	@Bean
	@Qualifier("discordApiClient")
	public WebClient discordApiClient() {
		return webClientBuilder.baseUrl("https://discord.com/api").build();
	}
}
