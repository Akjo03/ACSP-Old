package com.akjostudios.acsp.backend.data.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class DiscordAuthCodeRequest {
	private String clientId;
	private String scope;
	private String state;
	private String redirectUri;

	public String toUrl() {
		return "https://discord.com/oauth2/authorize?response_type=code&client_id=" + clientId + "&scope=" + scope + "&state=" + state + "&redirect_uri=" + redirectUri;
	}
}