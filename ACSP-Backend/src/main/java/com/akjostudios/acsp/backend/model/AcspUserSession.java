package com.akjostudios.acsp.backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("usersessions")
@RequiredArgsConstructor
@Getter
@Setter
public class AcspUserSession {
	@Id
	private String id;

	private String userId;
	private String status;

	private String accessToken;
	private String refreshToken;

	private String sessionId;
	private String sessionToken;
	private String sessionRefreshToken;

	private String salt;
}