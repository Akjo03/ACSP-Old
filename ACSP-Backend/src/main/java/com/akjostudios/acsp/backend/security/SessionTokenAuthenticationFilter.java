package com.akjostudios.acsp.backend.security;

import com.akjostudios.acsp.backend.model.AcspUser;
import com.akjostudios.acsp.backend.model.AcspUserSession;
import com.akjostudios.acsp.backend.repository.RoleRepository;
import com.akjostudios.acsp.backend.repository.UserRepository;
import com.akjostudios.acsp.backend.repository.UserSessionRepository;
import com.akjostudios.acsp.backend.services.SecurityService;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
public class SessionTokenAuthenticationFilter extends GenericFilterBean {
	private static final Logger LOGGER = LoggerManager.getLogger(SessionTokenAuthenticationFilter.class);

	private final SecurityService securityService;
	private final UserRepository userRepository;
	private final UserSessionRepository userSessionRepository;
	private final RoleRepository roleRepository;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);

		if (authHeader != null && authHeader.startsWith("Session ")) {
			String sessionToken = authHeader.substring(8);

			String sessionId = Arrays.stream(httpRequest.getCookies())
					.filter(cookie -> cookie.getName().equals("session"))
					.findFirst()
					.map(Cookie::getValue)
					.orElse(null);
			if (sessionId != null) {
				AcspUserSession session = userSessionRepository.findBySessionId(sessionId);
				if (session != null) {
					AcspUser user = userRepository.findByUserId(session.getUserId());
					if (user != null) {
						try {
							// TODO: Verify session token
						} catch (Exception e) {
							LOGGER.error("Session token verification failed", e);
						}
					}
				}
			}
		}

		chain.doFilter(request, response);
	}
}