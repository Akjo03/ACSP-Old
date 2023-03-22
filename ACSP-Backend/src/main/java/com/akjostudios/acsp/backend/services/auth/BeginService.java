package com.akjostudios.acsp.backend.services.auth;

import com.akjostudios.acsp.backend.dto.BeginAuthResponseDto;
import com.akjostudios.acsp.backend.model.BeginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BeginService {
	@Value("${application.base-url}")
	private String baseUrl;

	public BeginAuthResponseDto getBeginAuthReponseDto(BeginRequest beginRequest) {
		String link = baseUrl + "/api/auth/begin/authenticate?userId=" + beginRequest.getUserId() + "&code=" + makeUrlSafe(beginRequest.getCode());
		BeginAuthResponseDto beginAuthResponseDto = new BeginAuthResponseDto();
		beginAuthResponseDto.setAuthLink(link);
		return beginAuthResponseDto;
	}

	private String makeUrlSafe(String input) {
		return input.replace("/", "_").replace("+", "-");
	}

	private String makeUrlUnsafe(String input) {
		return input.replace("_", "/").replace("-", "+");
	}
}