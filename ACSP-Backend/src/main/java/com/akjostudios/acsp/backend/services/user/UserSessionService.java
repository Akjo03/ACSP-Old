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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserSessionService {
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
}