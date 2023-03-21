package com.akjostudios.acsp.backend.dto.oauth2.discord;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@SuppressWarnings("unused")
public class DiscordAuthResponse {
	@JsonDeserialize
	private String accessToken;

	@JsonDeserialize
	private String tokenType;

	@JsonDeserialize
	private int expiresIn;

	@JsonDeserialize
	private String refreshToken;

	@JsonDeserialize
	private String scope;

	@JsonCreator
	public DiscordAuthResponse(
			@JsonProperty("access_token") String accessToken,
			@JsonProperty("token_type") String tokenType,
			@JsonProperty("expires_in") int expiresIn,
			@JsonProperty("refresh_token") String refreshToken,
			@JsonProperty("scope") String scope) {
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.expiresIn = expiresIn;
		this.refreshToken = refreshToken;
		this.scope = scope;
	}
}