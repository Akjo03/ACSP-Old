package com.akjostudios.acsp.backend.services.user;

import com.akjostudios.acsp.backend.data.dto.user.UserDto;
import com.akjostudios.acsp.backend.data.dto.user.UserSessionStatusDto;
import com.akjostudios.acsp.backend.data.model.AcspUser;
import com.akjostudios.acsp.backend.data.model.AcspUserSession;
import com.akjostudios.acsp.backend.data.repository.UserRepository;
import com.akjostudios.acsp.backend.data.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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