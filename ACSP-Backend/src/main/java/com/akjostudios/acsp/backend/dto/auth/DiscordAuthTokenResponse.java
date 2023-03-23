package com.akjostudios.acsp.backend.dto.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class DiscordAuthTokenResponse {
	@JsonSerialize
	private String accessToken;

	@JsonSerialize
	private String tokenType;

	@JsonSerialize
	private int expiresIn;

	@JsonSerialize
	private String refreshToken;

	@JsonSerialize
	private String scope;

	@JsonCreator
	public DiscordAuthTokenResponse(
			@JsonProperty("access_token") String accessToken,
			@JsonProperty("token_type") String tokenType,
			@JsonProperty("expires_in") int expiresIn,
			@JsonProperty("refresh_token") String refreshToken,
			@JsonProperty("scope") String scope
	) {
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.expiresIn = expiresIn;
		this.refreshToken = refreshToken;
		this.scope = scope;
	}
}