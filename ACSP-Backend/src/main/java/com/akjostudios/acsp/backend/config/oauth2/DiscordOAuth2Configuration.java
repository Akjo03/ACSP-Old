package com.akjostudios.acsp.backend.config.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.oauth2.discord")
@Getter
@Setter
public class DiscordOAuth2Configuration {
	private String clientId;
	private String clientSecret;
	private String redirectUri;
}