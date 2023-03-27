package com.akjostudios.acsp.backend.services.user;

import com.akjostudios.acsp.backend.config.SecurityConfig;
import com.akjostudios.acsp.backend.dto.user.UserDto;
import com.akjostudios.acsp.backend.dto.user.UserSessionRefreshDto;
import com.akjostudios.acsp.backend.dto.user.UserSessionStatusDto;
import com.akjostudios.acsp.backend.model.AcspUser;
import com.akjostudios.acsp.backend.model.AcspUserSession;
import com.akjostudios.acsp.backend.repository.UserRepository;
import com.akjostudios.acsp.backend.repository.UserSessionRepository;
import com.akjostudios.acsp.backend.services.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@RequiredArgsConstructor
public class UserSessionService {
	private static final int SESSION_TOKEN_EXPIRY = 60; // 1 minute
	private static final int SESSION_REFRESH_TOKEN_EXPIRY = 60 * 60 * 24 * 7; // 1 week

	private final SecurityConfig securityConfig;
	private final SecurityService securityService;

	private final UserSessionRepository userSessionRepository;
	private final UserRepository userRepository;

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

	public UserSessionRefreshDto getRefreshToken(String sessionId) {
		AcspUserSession session = userSessionRepository.findBySessionId(sessionId);
		if (session == null) {
			return null;
		}

		return null;
	}
}