package com.akjostudios.acsp.backend.services.auth;

import com.akjostudios.acsp.backend.config.ApplicationConfig;
import com.akjostudios.acsp.backend.config.SecurityConfig;
import com.akjostudios.acsp.backend.dto.auth.BeginLinkResponseDto;
import com.akjostudios.acsp.backend.dto.auth.DiscordAuthCodeRequest;
import com.akjostudios.acsp.backend.dto.auth.DiscordAuthTokenRequest;
import com.akjostudios.acsp.backend.dto.auth.DiscordAuthTokenResponse;
import com.akjostudios.acsp.backend.dto.discord.DiscordUserResponse;
import com.akjostudios.acsp.backend.model.*;
import com.akjostudios.acsp.backend.repository.UserRepository;
import com.akjostudios.acsp.backend.repository.UserSessionRepository;
import com.akjostudios.acsp.backend.services.SecurityService;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BeginService {
	private static final Logger LOGGER = LoggerManager.getLogger(BeginService.class);

	@Value("${application.base-url}")
	private String baseUrl;

	@Value("${application.oauth2.discord.client-id}")
	private String discordClientId;

	@Value("${application.oauth2.discord.redirect-uri}")
	private String discordRedirectUri;

	private final SecurityService securityService;
	private final SecurityConfig securityConfig;

	private final UserRepository userRepository;
	private final UserSessionRepository userSessionRepository;

	private final DiscordTokenService discordTokenService;
	@Qualifier("discordTokenClient")
	private final WebClient discordTokenClient;
	@Qualifier("discordApiClient")
	private final WebClient discordApiClient;

	private final ApplicationConfig applicationConfig;

	public String makeUrlSafe(String input) {
		return input.replace("/", "_").replace("+", "-").replace("=", "~");
	}

	public String makeUrlUnsafe(String input) {
		return input.replace("_", "/").replace("-", "+").replace("~", "=");
	}

	public BeginLinkResponseDto getBeginAuthLinkReponseDto(BeginRequest beginRequest) {
		String link = baseUrl + "/api/auth/begin/authenticate?userId=" + beginRequest.getUserId() + "&code=" + makeUrlSafe(beginRequest.getCode());
		BeginLinkResponseDto beginAuthResponseDto = new BeginLinkResponseDto();
		beginAuthResponseDto.setBeginLink(link);
		return beginAuthResponseDto;
	}

	public BeginLinkResponseDto getBeginOnboardingLinkResponseDto(String userId) {
		BeginLinkResponseDto beginOnboardingResponseDto = new BeginLinkResponseDto();
		beginOnboardingResponseDto.setBeginLink(applicationConfig.getAppBaseUrl() + "/onboarding?userId=" + userId);
		return beginOnboardingResponseDto;
	}

	public BeginLinkResponseDto getBeginDashboardLinkResponseDto(String userId) {
		String link = "#"; // TODO: Add dashboard link
		BeginLinkResponseDto beginOnboardingResponseDto = new BeginLinkResponseDto();
		beginOnboardingResponseDto.setBeginLink(link);
		return beginOnboardingResponseDto;
	}

	public BeginRequest createBeginRequest(String userId, String code, String salt) {
		BeginRequest beginRequest = new BeginRequest();
		beginRequest.setUserId(userId);
		beginRequest.setCode(code);
		beginRequest.setSalt(salt);
		return beginRequest;
	}

	public ResponseEntity<BeginLinkResponseDto> getExistingUserBeginLinkResponse(AcspUser acspUser, AcspUserSession acspUserSession) {
		if (acspUser != null) {
			if (acspUserSession != null) { // User has an existing session
				// Respond with the begin link based on the session status
				BeginLinkResponseDto beginLinkResponseDto = acspUserSession.getStatus().equals(AcspUserSessionStatus.ONBOARDING.getStatus())
						? getBeginOnboardingLinkResponseDto(acspUser.getUserId())
						: getBeginDashboardLinkResponseDto(acspUser.getUserId());
				return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body(beginLinkResponseDto);
			} else { // User has no existing session
				userRepository.delete(acspUser);
				return ResponseEntity.status(HttpStatus.GONE).build();
			}
		}
		return null;
	}

	public ResponseEntity<String> getOnboardingRedirectResponse(AcspUserSession acspUserSession) {
		HttpHeaders redirectHeaders = new HttpHeaders();
		redirectHeaders.add("Location", applicationConfig.getAppBaseUrl() + "/onboarding");
		redirectHeaders.add("Set-Cookie", "session=" + acspUserSession.getSessionId() + "; Path=/; SameSite=Strict; Secure");
		redirectHeaders.add("Set-Cookie", "session_token=" + acspUserSession.getSessionToken() + "; Path=/; SameSite=Strict; Secure");
		return new ResponseEntity<>(redirectHeaders, HttpStatus.SEE_OTHER);
	}

	public ResponseEntity<String> getDashboardRedirectResponse() {
		HttpHeaders redirectHeaders = new HttpHeaders();
		redirectHeaders.add("Location", "#"); // TODO: Add dashboard link
		return new ResponseEntity<>(redirectHeaders, HttpStatus.SEE_OTHER);
	}

	public ResponseEntity<String> getExistingUserAndSessionResponse(AcspUserSession acspUserSession) {
		if (acspUserSession != null) {
			return acspUserSession.getStatus().equals(AcspUserSessionStatus.ONBOARDING.getStatus())
					? getOnboardingRedirectResponse(acspUserSession)
					: getDashboardRedirectResponse();
		}
		return null;
	}

	public String getAlreadyAuthenticatedMessage(ResponseEntity<BeginLinkResponseDto> existingUserResponse) {
		return "You are already authenticated! Use <a href=\""
				+ Objects.requireNonNull(existingUserResponse.getBody()).getBeginLink()
				+ "\">this link</a> to continue."
				+ "<br /><br />"
				+ "Du bist schon authentifiziert! Benutze <a href=\""
				+ Objects.requireNonNull(existingUserResponse.getBody()).getBeginLink()
				+ "\">diesen Link</a> um fortzufahren.";
	}

	public String getMissingBeginRequestMessage() {
		return "No begin request was found for this user! Please issue the !begin command again."
				+ "<br /><br />"
				+ "Es wurde kein Beginn-Anfrage für diesen Benutzer gefunden! Bitte führe den !begin Befehl erneut aus.";
	}

	public String getInvalidStateMessage() {
		return "The auth system is either overloaded or the begin request is invalid."
				+ "<br /><br />"
				+ "Das Authentifizierungssystem ist entweder überlastet oder die Beginn-Anfrage ist ungültig.";
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

	public DiscordAuthTokenResponse getDiscordAuthTokenResponse(String code) {
		DiscordAuthTokenRequest discordAuthTokenRequest = discordTokenService.getDiscordAuthTokenRequest(code);
		if (discordAuthTokenRequest == null) {
			return null;
		}
		DiscordAuthTokenResponse discordAuthTokenResponse;
		try {
			discordAuthTokenResponse = discordTokenClient.post()
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
					.body(BodyInserters.fromFormData(discordAuthTokenRequest.toMultiValueMap()))
					.retrieve()
					.bodyToMono(DiscordAuthTokenResponse.class)
					.block();
		} catch (Exception e) {
			return null;
		}
		return discordAuthTokenResponse;
	}

	public DiscordUserResponse getDiscordUserResponse(DiscordAuthTokenResponse discordAuthTokenResponse) {
		Optional<DiscordUserResponse> discordUserResponse;
		try {
			discordUserResponse = discordApiClient.get()
					.uri("/users/@me")
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + discordAuthTokenResponse.getAccessToken())
					.retrieve()
					.bodyToMono(DiscordUserResponse.class)
					.blockOptional();
		} catch (Exception e) {
			return null;
		}
		if (discordUserResponse.isEmpty()) {
			return null;
		}
		DiscordUserResponse discordUser = discordUserResponse.get();
		if (discordUser.getId() == null || discordUser.getUsername() == null || discordUser.getDiscriminator() == null) {
			return null;
		}
		return discordUser;
	}

	public AcspUserSession getUserSessionForUser(String userId) {
		return userSessionRepository.findByUserId(userId);
	}

	public ResponseEntity<String> getOnboardingResponse(AcspUser acspUser, AcspUserSession acspUserSession, DiscordUserResponse discordUserResponse, DiscordAuthTokenResponse discordTokenResponse) {
		if (acspUser != null) {
			if (acspUserSession != null) {
				return getExistingUserAndSessionResponse(acspUserSession);
			} else {
				AcspUserSession newAcspUserSession = createOnboardingSession(acspUser, discordTokenResponse);
				if (newAcspUserSession == null) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}
				userSessionRepository.save(newAcspUserSession);
				return getOnboardingRedirectResponse(newAcspUserSession);
			}
		} else {
			if (acspUserSession != null) {
				userSessionRepository.delete(acspUserSession);
			}

			AcspUser newAcspUser = createUserFromUserResponse(discordUserResponse);
			if (newAcspUser == null) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			userRepository.save(newAcspUser);

			AcspUserSession newAcspUserSession = createOnboardingSession(newAcspUser, discordTokenResponse);
			if (newAcspUserSession == null) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			userSessionRepository.save(newAcspUserSession);

			return getOnboardingRedirectResponse(newAcspUserSession);
		}
	}

	public AcspUser createUserFromUserResponse(DiscordUserResponse userResponse) {
		AcspUser acspUser = new AcspUser();
		acspUser.setUserId(userResponse.getId());
		acspUser.setEmail(userResponse.getEmail());
		acspUser.setRole(AcspRoles.USER.getRole());

		return acspUser;
	}

	public AcspUserSession createOnboardingSession(AcspUser user, DiscordAuthTokenResponse discordTokenResponse) {
		AcspUserSession acspUserSession = new AcspUserSession();
		acspUserSession.setUserId(user.getUserId());
		acspUserSession.setStatus(AcspUserSessionStatus.ONBOARDING.getStatus());

		String salt = securityService.generateSalt();
		acspUserSession.setSalt(salt);

		IvParameterSpec ivParameterSpec = securityService.generateIv();
		acspUserSession.setIv(new String(Base64.getEncoder().encode(ivParameterSpec.getIV())));
		try {
			SecretKey secretKey = securityService.getKeyFromPassword(securityConfig.getDiscordEncryptionKey(), salt);
			String encryptedAccessToken = securityService.encrypt(discordTokenResponse.getAccessToken(), secretKey, ivParameterSpec);
			String encryptedRefreshToken = securityService.encrypt(discordTokenResponse.getRefreshToken(), secretKey, ivParameterSpec);

			acspUserSession.setAccessToken(encryptedAccessToken);
			acspUserSession.setRefreshToken(encryptedRefreshToken);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		try {
			String sessionId = UUID.randomUUID().toString();
			KeyPair keyPair = securityService.generateKeyPair();
			String sessionKeySecret = securityConfig.getSessionKeySecret();

			SecretKey secretKey = securityService.getKeyFromPassword(sessionKeySecret, salt);
			String encryptedSessionKey = securityService.encrypt(new String(Base64.getEncoder().encode(keyPair.getPublic().getEncoded())), secretKey, ivParameterSpec);

			String sessionToken = securityService.generateToken(sessionId, user.getUserId(), 60*60, (RSAPrivateKey) keyPair.getPrivate());
			String sessionRefreshToken = securityService.generateToken(sessionId, user.getUserId(), 60*60*24*7, (RSAPrivateKey) keyPair.getPrivate());

			acspUserSession.setSessionId(sessionId);
			acspUserSession.setSessionKey(encryptedSessionKey);
			acspUserSession.setSessionToken(sessionToken);
			acspUserSession.setSessionRefreshToken(sessionRefreshToken);

			acspUserSession.setSessionId(sessionId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return acspUserSession;
	}
}