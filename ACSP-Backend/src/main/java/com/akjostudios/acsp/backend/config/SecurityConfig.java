package com.akjostudios.acsp.backend.config;

import com.akjostudios.acsp.backend.data.repository.RoleRepository;
import com.akjostudios.acsp.backend.data.repository.UserRepository;
import com.akjostudios.acsp.backend.data.repository.UserSessionRepository;
import com.akjostudios.acsp.backend.security.BotAuthenticationFilter;
import com.akjostudios.acsp.backend.security.SessionTokenAuthenticationFilter;
import com.akjostudios.acsp.backend.services.security.KeystoreService;
import com.akjostudios.acsp.backend.services.security.SecurityService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

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

	@Value("${application.security.session-key-secret}")
	private String sessionKeySecret;

	private final ApplicationConfig applicationConfig;
	private final SecurityService securityService;
	private final KeystoreService keystoreService;
	private final UserRepository userRepository;
	private final UserSessionRepository userSessionRepository;
	private final RoleRepository roleRepository;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/api/auth/begin").permitAll()
						.requestMatchers("/api/auth/begin/authenticate").permitAll()
						.requestMatchers("/api/auth/begin/code").permitAll()
						.requestMatchers("/api/user/onboarding").permitAll()
						.anyRequest().authenticated()
				)
				.addFilterBefore(new BotAuthenticationFilter(botApiKey), BasicAuthenticationFilter.class)
				.addFilterBefore(new SessionTokenAuthenticationFilter(this, securityService, keystoreService, userRepository, userSessionRepository, roleRepository), BasicAuthenticationFilter.class)
				.httpBasic().disable()
				.formLogin().disable()
				.cors().configurationSource(request -> {
					CorsConfiguration corsConfiguration = new CorsConfiguration();
					corsConfiguration.addAllowedOrigin(applicationConfig.getAppBaseUrl());
					corsConfiguration.addAllowedOrigin(applicationConfig.getDiscordBotUrl());
					corsConfiguration.addAllowedHeader("*");
					corsConfiguration.addAllowedMethod("*");
					corsConfiguration.setAllowCredentials(true);
					return corsConfiguration;
				}).and()
				.exceptionHandling()
				.and().build();
	}
}