package com.akjostudios.acsp.backend.security;

import com.akjostudios.acsp.backend.config.SecurityConfig;
import com.akjostudios.acsp.backend.data.model.AcspRole;
import com.akjostudios.acsp.backend.data.model.AcspUser;
import com.akjostudios.acsp.backend.data.model.AcspUserSession;
import com.akjostudios.acsp.backend.data.repository.RoleRepository;
import com.akjostudios.acsp.backend.data.repository.UserRepository;
import com.akjostudios.acsp.backend.data.repository.UserSessionRepository;
import com.akjostudios.acsp.backend.services.security.KeystoreService;
import com.akjostudios.acsp.backend.services.security.SecurityService;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashSet;

@RequiredArgsConstructor
public class SessionTokenAuthenticationFilter extends GenericFilterBean {
	private static final Logger LOGGER = LoggerManager.getLogger(SessionTokenAuthenticationFilter.class);

	private final SecurityConfig securityConfig;
	private final SecurityService securityService;
	private final KeystoreService keystoreService;
	private final UserRepository userRepository;
	private final UserSessionRepository userSessionRepository;
	private final RoleRepository roleRepository;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);

		if (httpRequest.getMethod().equals(HttpMethod.OPTIONS.name())) {
			chain.doFilter(request, response);
			return;
		}

		if (authHeader != null && authHeader.startsWith("Session ")) {
			String reqSessionId = httpRequest.getHeader("X-Session-ID");
			String reqSessionToken = authHeader.substring(8);

			if (reqSessionId != null) {
				AcspUserSession session = userSessionRepository.findBySessionId(reqSessionId);
				if (session != null) {
					AcspUser user = userRepository.findByUserId(session.getUserId());
					if (user != null) {
						try {
							String sessionToken = session.getSessionToken();
							if (sessionToken != null) {
								if (sessionToken.equals(reqSessionToken)) {
									String encPublicKey = session.getSessionKey();
									String encIv = session.getIv();
									String encSalt = session.getSalt();

									SecretKey key = securityService.getKeyFromPassword(securityConfig.getSessionKeySecret(), encSalt);
									PublicKey publicKey = securityService.getPublicKey(encPublicKey, key, securityService.getIv(encIv));

									KeyStore keystore = keystoreService.getKeystore();
									PrivateKey privateKey = keystoreService.getPrivateKey(keystore, session.getSessionId());
									PublicKey generatedPublicKey = securityService.getPublicKey(privateKey);

									if (Arrays.equals(generatedPublicKey.getEncoded(), publicKey.getEncoded())) {
										Claims claims = securityService.verifyToken(reqSessionToken, publicKey);
										if (claims != null) {
											if (claims.getIssuer().equals(user.getUserId())) {
												AcspRole role = roleRepository.findByName(user.getRole());
												HashSet<GrantedAuthority> authorities = new HashSet<>();
												role.getPermissions().forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
												UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getUserId(), null, authorities);
												SecurityContextHolder.getContext().setAuthentication(authentication);
											}
										}
									}
								}
							}
						} catch (ExpiredJwtException ignored) {
						} catch (Exception e) {
							LOGGER.error("Error while authenticating session token for user " + user.getUserId(), e);
						}
					}
				}
			}
		}

		chain.doFilter(request, response);
	}
}