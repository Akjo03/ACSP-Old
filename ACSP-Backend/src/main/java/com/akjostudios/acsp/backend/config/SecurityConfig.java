package com.akjostudios.acsp.backend.config;

import com.akjostudios.acsp.backend.security.BotAuthenticationFilter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@Getter
@Setter
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	@Value("${application.security.discord-encryption-key}")
	private String discordEncryptionKey;

	@Value("${application.security.bot-api-key}")
	private String botApiKey;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.csrf().disable()
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/begin").permitAll()
						.requestMatchers("/api/auth/begin/authenticate").permitAll()
						.requestMatchers("/api/auth/begin/code").permitAll()
						.anyRequest().authenticated()
				)
				.addFilterBefore(new BotAuthenticationFilter(botApiKey), BasicAuthenticationFilter.class)
				.httpBasic().disable()
				.formLogin().disable()
				.exceptionHandling()
				.and().build();
	}
}