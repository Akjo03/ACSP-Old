package com.akjostudios.acsp.backend.services.auth;

import com.akjostudios.acsp.backend.config.SecurityConfig;
import com.akjostudios.acsp.backend.dto.auth.BeginAuthResponseDto;
import com.akjostudios.acsp.backend.dto.auth.DiscordAuthCodeRequest;
import com.akjostudios.acsp.backend.dto.auth.DiscordAuthTokenResponse;
import com.akjostudios.acsp.backend.dto.discord.DiscordUserResponse;
import com.akjostudios.acsp.backend.model.AcspUser;
import com.akjostudios.acsp.backend.model.AcspUserSession;
import com.akjostudios.acsp.backend.model.AcspUserSessionStatus;
import com.akjostudios.acsp.backend.model.BeginRequest;
import com.akjostudios.acsp.backend.services.SecurityService;
import io.github.akjo03.lib.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BeginService {
	@Value("${application.base-url}")
	private String baseUrl;

	@Value("${application.oauth2.discord.client-id}")
	private String discordClientId;

	@Value("${application.oauth2.discord.client-secret}")
	private String discordClientSecret;

	@Value("${application.oauth2.discord.redirect-uri}")
	private String discordRedirectUri;

	private final SecurityService securityService;
	private final SecurityConfig securityConfig;

	public BeginAuthResponseDto getBeginAuthReponseDto(BeginRequest beginRequest) {
		String link = baseUrl + "/api/auth/begin/authenticate?userId=" + beginRequest.getUserId() + "&code=" + makeUrlSafe(beginRequest.getCode());
		BeginAuthResponseDto beginAuthResponseDto = new BeginAuthResponseDto();
		beginAuthResponseDto.setAuthLink(link);
		return beginAuthResponseDto;
	}

	public String makeUrlSafe(String input) {
		return input.replace("/", "_").replace("+", "-").replace("=", "~");
	}

	public String makeUrlUnsafe(String input) {
		return input.replace("_", "/").replace("-", "+").replace("~", "=");
	}

	public DiscordAuthCodeRequest getDiscordAuthCodeRequest(String code) {
		DiscordAuthCodeRequest discordAuthCodeRequest = new DiscordAuthCodeRequest();
		discordAuthCodeRequest.setClientId(discordClientId);
		discordAuthCodeRequest.setRedirectUri(discordRedirectUri);
		discordAuthCodeRequest.setScope(Stream.of(
				"identify", "email", "guilds"
		).reduce((s1, s2) -> s1 + "%20" + s2).orElse(""));
		discordAuthCodeRequest.setState(code);
		return discordAuthCodeRequest;
	}

	public AcspUser createUserFromUserResponse(DiscordUserResponse userResponse) {
		AcspUser acspUser = new AcspUser();
		acspUser.setUserId(userResponse.getId());

		return acspUser;
	}

	public AcspUserSession createOnboardingSession(AcspUser user, DiscordAuthTokenResponse discordTokenResponse) {
		AcspUserSession acspUserSession = new AcspUserSession();
		acspUserSession.setUserId(user.getUserId());
		acspUserSession.setStatus(AcspUserSessionStatus.ONBOARDING.getStatus());

		String salt = securityService.generateSalt();
		acspUserSession.setSalt(salt);

		IvParameterSpec ivParameterSpec = securityService.generateIv();
		try {
			SecretKey secretKey = securityService.getKeyFromPassword(securityConfig.getDiscordEncryptionKey(), salt);
			String encryptedAccessToken = securityService.encrypt(discordTokenResponse.getAccessToken(), secretKey, ivParameterSpec);
			String encryptedRefreshToken = securityService.encrypt(discordTokenResponse.getRefreshToken(), secretKey, ivParameterSpec);

			acspUserSession.setAccessToken(encryptedAccessToken);
			acspUserSession.setRefreshToken(encryptedRefreshToken);
		} catch (Exception e) {
			return null;
		}

		return acspUserSession;
	}

	public ResponseEntity<String> startOnboardingProcess() {
		return ResponseEntity.ok("Onboarding process started");
	}
}