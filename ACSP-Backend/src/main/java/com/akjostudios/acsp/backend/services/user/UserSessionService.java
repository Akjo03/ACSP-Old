package com.akjostudios.acsp.backend.services.user;

import com.akjostudios.acsp.backend.config.SecurityConfig;
import com.akjostudios.acsp.backend.data.dto.user.UserDto;
import com.akjostudios.acsp.backend.data.dto.user.UserSessionRefreshDto;
import com.akjostudios.acsp.backend.data.dto.user.UserSessionStatusDto;
import com.akjostudios.acsp.backend.data.model.AcspUser;
import com.akjostudios.acsp.backend.data.model.AcspUserSession;
import com.akjostudios.acsp.backend.data.repository.UserRepository;
import com.akjostudios.acsp.backend.data.repository.UserSessionRepository;
import com.akjostudios.acsp.backend.services.security.KeystoreService;
import com.akjostudios.acsp.backend.services.security.SecurityService;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.KeyStore;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;

@Service
@RequiredArgsConstructor
public class UserSessionService {
	private static final Logger LOGGER = LoggerManager.getLogger(UserSessionService.class);

	private final UserSessionRepository userSessionRepository;
	private final UserRepository userRepository;

	private final SecurityService securityService;
	private final KeystoreService keystoreService;

	public UserSessionStatusDto getUserSessionStatus(String userId) {
		AcspUserSession acspUserSession = userSessionRepository.findByUserId(userId);
		if (acspUserSession == null) {
			return null;
		}

		UserSessionStatusDto userSessionStatusDto = new UserSessionStatusDto();
		userSessionStatusDto.setUserId(acspUserSession.getUserId());
		userSessionStatusDto.setSessionId(acspUserSession.getSessionId());
		userSessionStatusDto.setSessionStatus(acspUserSession.getStatus());

		return userSessionStatusDto;
	}

	public UserDto getUserBySessionId(String sessionId) {
		AcspUserSession session = userSessionRepository.findBySessionId(sessionId);
		if (session == null) {
			return null;
		}

		AcspUser user = userRepository.findByUserId(session.getUserId());
		if (user == null) {
			return null;
		}

		UserDto userDto = new UserDto();
		userDto.setUser(user);
		return userDto;
	}

	public UserSessionRefreshDto refreshUserSession(String sessionId) {
		AcspUserSession acspUserSession = userSessionRepository.findBySessionId(sessionId);
		if (acspUserSession == null) {
			return null;
		}

		UserSessionRefreshDto userSessionRefreshDto = new UserSessionRefreshDto();
		userSessionRefreshDto.setUserId(acspUserSession.getUserId());
		userSessionRefreshDto.setSessionId(acspUserSession.getSessionId());

		try {
			KeyStore keyStore = keystoreService.getKeystore();
			RSAPrivateKey privateKey = (RSAPrivateKey) keystoreService.getKey(keyStore, acspUserSession.getSessionId());
			PublicKey publicKey = securityService.getPublicKey(privateKey);

			Claims claims = securityService.verifyToken(acspUserSession.getSessionRefreshToken(), publicKey);
			if (claims == null) {
				return null;
			}

			String newSessionToken = securityService.generateToken(
					acspUserSession.getSessionId(),
					acspUserSession.getUserId(),
					SecurityConfig.SESSION_TOKEN_EXPIRY,
					privateKey
			);

			acspUserSession.setSessionToken(newSessionToken);
			userSessionRepository.save(acspUserSession);

			userSessionRefreshDto.setSessionToken(newSessionToken);
		} catch (ExpiredJwtException e) {
			LOGGER.warn("Session refresh token expired for user: " + acspUserSession.getUserId());
			return null; // TODO: Handle session refresh token expiry (logout user)
		} catch (Exception e) {
			LOGGER.error("Error while refreshing user session: " + acspUserSession.getUserId(), e);
			return null;
		}

		return userSessionRefreshDto;
	}

	public void deleteUserSession(String sessionId) {
		userSessionRepository.deleteBySessionId(sessionId);

		try {
			KeyStore keyStore = keystoreService.getKeystore();
			keystoreService.deleteKey(keyStore, sessionId);
		} catch (Exception e) {
			LOGGER.error("Error while deleting user session: " + sessionId, e);
		}
	}
}