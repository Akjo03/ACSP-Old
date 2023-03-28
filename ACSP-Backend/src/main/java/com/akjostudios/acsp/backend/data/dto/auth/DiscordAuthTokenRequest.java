package com.akjostudios.acsp.backend.data.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RequiredArgsConstructor
@Getter
@Setter
public class DiscordAuthTokenRequest {
	private String clientId;
	private String clientSecret;
	private String grantType;
	private String code;
	private String redirectUri;

	public MultiValueMap<String, String> toMultiValueMap() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("client_id", clientId);
		map.add("client_secret", clientSecret);
		map.add("grant_type", grantType);
		map.add("code", code);
		map.add("redirect_uri", redirectUri);
		return map;
	}
}