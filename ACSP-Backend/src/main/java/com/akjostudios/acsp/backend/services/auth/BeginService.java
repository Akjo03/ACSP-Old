package com.akjostudios.acsp.backend.services.auth;

import com.akjostudios.acsp.backend.config.ApplicationConfig;
import com.akjostudios.acsp.backend.config.SecurityConfig;
import com.akjostudios.acsp.backend.config.auth.AcspSecretConfiguration;
import com.akjostudios.acsp.backend.constants.CookieConstants;
import com.akjostudios.acsp.backend.data.dto.auth.BeginLinkResponseDto;
import com.akjostudios.acsp.backend.data.dto.auth.DiscordAuthTokenResponse;
import com.akjostudios.acsp.backend.data.dto.discord.DiscordUserResponse;
import com.akjostudios.acsp.backend.data.model.*;
import com.akjostudios.acsp.backend.data.repository.UserRepository;
import com.akjostudios.acsp.backend.data.repository.UserSessionRepository;
import com.akjostudios.acsp.backend.services.security.KeystoreService;
import com.akjostudios.acsp.backend.services.security.SecurityService;
import com.akjostudios.acsp.backend.util.RandomStrings;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BeginService {
	private static final Logger LOGGER = LoggerManager.getLogger(BeginService.class);

	@Value("${application.base-url}")
	private String baseUrl;

	private final SecurityService securityService;
	private final KeystoreService keystoreService;
	private final SecurityConfig securityConfig;
	private final AcspSecretConfiguration acspSecretConfiguration;

	private final UserRepository userRepository;
	private final UserSessionRepository userSessionRepository;

	private final ApplicationConfig applicationConfig;

	public String makeUrlSafe(String input) {
		return input.replace("/", "_").replace("+", "-").replace("=", "~");
	}

	public String makeUrlUnsafe(String input) {
		return input.replace("_", "/").replace("-", "+").replace("~", "=");
	}

	public BeginLinkResponseDto getBeginAuthLinkReponseDto(AcspBeginRequest beginRequest) {
		String link = baseUrl + "/api/auth/begin/authenticate?userId=" + beginRequest.getUserId() + "&code=" + makeUrlSafe(beginRequest.getCode());
		BeginLinkResponseDto beginAuthResponseDto = new BeginLinkResponseDto();
		beginAuthResponseDto.setBeginLink(link);
		return beginAuthResponseDto;
	}

	public BeginLinkResponseDto getBeginOnboardingLinkResponseDto(String userId) {
		BeginLinkResponseDto beginOnboardingResponseDto = new BeginLinkResponseDto();
		beginOnboardingResponseDto.setBeginLink(applicationConfig.getBaseUrl() + "/api/user/onboarding?userId=" + userId + "&secret=" + acspSecretConfiguration.getAcspBeginSecret());
		return beginOnboardingResponseDto;
	}

	public BeginLinkResponseDto getBeginDashboardLinkResponseDto() {
		String link = "#"; // TODO: Add dashboard link
		BeginLinkResponseDto beginOnboardingResponseDto = new BeginLinkResponseDto();
		beginOnboardingResponseDto.setBeginLink(link);
		return beginOnboardingResponseDto;
	}

	public AcspBeginRequest createBeginRequest(String userId, String code, String salt) {
		AcspBeginRequest beginRequest = new AcspBeginRequest();
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
						: getBeginDashboardLinkResponseDto();
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
		redirectHeaders.add("Set-Cookie", CookieConstants.SESSION_ID + "=" + acspUserSession.getSessionId() + "; Path=/; SameSite=Strict; Secure");
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
		acspUser.setAvatar(userResponse.getAvatar());
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
			String sessionId = RandomStrings.generate(24);
			KeyPair keyPair = securityService.generateKeyPair();
			String sessionKeySecret = securityConfig.getSessionKeySecret();

			SecretKey secretKey = securityService.getKeyFromPassword(sessionKeySecret, salt);
			String encryptedSessionKey = securityService.encrypt(new String(Base64.getEncoder().encode(keyPair.getPublic().getEncoded())), secretKey, ivParameterSpec);

			KeyStore keystore = keystoreService.getKeystore();
			if (keystore == null) {
				return null;
			}

			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			keystoreService.setKey(keystore, sessionId, privateKey);
			keystoreService.saveKeystore(keystore);

			String sessionToken = securityService.generateToken(
					sessionId,
					user.getUserId(),
					SecurityConfig.SESSION_TOKEN_EXPIRY,
					privateKey
			);
			String sessionRefreshToken = securityService.generateToken(
					sessionId,
					user.getUserId(),
					SecurityConfig.SESSION_REFRESH_TOKEN_EXPIRY,
					privateKey
			);

			acspUserSession.setSessionId(sessionId);
			acspUserSession.setSessionKey(encryptedSessionKey);
			acspUserSession.setSessionToken(sessionToken);
			acspUserSession.setSessionRefreshToken(sessionRefreshToken);

			acspUserSession.setSessionId(sessionId);
		} catch (Exception e) {
			LOGGER.info("Error creating onboarding session!", e);
			return null;
		}

		return acspUserSession;
	}
}