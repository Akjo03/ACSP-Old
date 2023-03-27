package com.akjostudios.acsp.backend.dto.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class UserSessionRefreshDto {
	private String userId;
	private String sessionId;
	private String accessToken;
	private String refreshToken;
}