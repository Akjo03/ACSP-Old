package com.akjostudios.acsp.backend.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConfigurationProperties("application.security")
@RequiredArgsConstructor
@Getter
@Setter
@EnableWebSecurity
public class SecurityConfig {
	private String discordEncryptionKey;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.csrf().disable()
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/begin").permitAll()
						.requestMatchers("/api/auth/begin/authenticate").permitAll()
						.requestMatchers("/api/auth/begin/code").permitAll()
						.anyRequest().authenticated()
				).httpBasic().disable()
				.formLogin().disable()
				.exceptionHandling()
				.and().build();
	}
}