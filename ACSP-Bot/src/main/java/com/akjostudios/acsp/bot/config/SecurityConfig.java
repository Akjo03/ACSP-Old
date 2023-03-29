package com.akjostudios.acsp.bot.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@RequiredArgsConstructor
@Getter
@Setter
@EnableWebSecurity
public class SecurityConfig {
	private final ExternalServiceConfig externalServiceConfig;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.requiresChannel(channel -> channel.anyRequest().requiresSecure())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/begin").permitAll()
						.anyRequest().authenticated()
				)
				.httpBasic().disable()
				.formLogin().disable()
				.cors().configurationSource(request -> {
					CorsConfiguration corsConfiguration = new CorsConfiguration();
					corsConfiguration.addAllowedOrigin(externalServiceConfig.getBackendBaseUrl());
					corsConfiguration.addAllowedHeader("*");
					corsConfiguration.addAllowedMethod("*");
					corsConfiguration.setAllowCredentials(true);
					return corsConfiguration;
				})
				.and().exceptionHandling()
				.and().build();
	}
}