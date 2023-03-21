package com.akjostudios.acsp.backend.config.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.secrets")
@Getter
@Setter
public class AcspSecretConfiguration {
	private String acspBeginSecret;
}