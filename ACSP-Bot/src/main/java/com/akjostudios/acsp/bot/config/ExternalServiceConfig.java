package com.akjostudios.acsp.bot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.services")
@Getter
@Setter
public class ExternalServiceConfig {
	private String appBaseUrl;
	private String backendBaseUrl;
}