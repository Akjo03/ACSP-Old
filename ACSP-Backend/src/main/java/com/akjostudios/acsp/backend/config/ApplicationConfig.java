package com.akjostudios.acsp.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("application")
@Getter
@Setter
public class ApplicationConfig {
	private String baseUrl;
	private String discordBotUrl;
	private String appBaseUrl;
	private String discordServerUrl;
}