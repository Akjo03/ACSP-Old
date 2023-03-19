package com.akjostudios.acsp.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.csrf().disable()
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/oauth2/login/**").permitAll()
						.anyRequest().authenticated()
				).httpBasic().disable()
				.formLogin().disable()
				.exceptionHandling()
				.and().build();
	}
}