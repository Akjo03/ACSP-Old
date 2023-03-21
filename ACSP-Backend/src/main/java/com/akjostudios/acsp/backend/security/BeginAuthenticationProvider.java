package com.akjostudios.acsp.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class BeginAuthenticationProvider implements AuthenticationProvider {
	@Value("${application.secrets.acsp-begin-secret}")
	private String acspBeginSecret;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String token = (String) authentication.getPrincipal();
		if (token.equals(acspBeginSecret)) {
			return new PreAuthenticatedAuthenticationToken(token, null);
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(PreAuthenticatedAuthenticationToken.class);
	}
}