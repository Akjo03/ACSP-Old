package com.akjostudios.acsp.backend.controller.proxy;

import com.akjostudios.acsp.backend.config.ApplicationConfig;
import com.akjostudios.acsp.backend.data.dto.user.UserSessionRefreshDto;
import com.akjostudios.acsp.backend.data.model.AcspUserSession;
import com.akjostudios.acsp.backend.data.repository.UserSessionRepository;
import com.akjostudios.acsp.backend.services.user.UserSessionService;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/proxy/**")
public class ProxyController {
	private static final Logger LOGGER = LoggerManager.getLogger(ProxyController.class);

	@Qualifier("selfClient")
	private final WebClient selfClient;

	private final ApplicationConfig applicationConfig;

	private final UserSessionService userSessionService;
	private final UserSessionRepository userSessionRepository;

	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
	public ResponseEntity<byte[]> proxyRequest(
			@CookieValue("session_id") String sessionId,
			HttpMethod method,
			HttpServletRequest request
	) throws IOException {
		String targetApiUrl = applicationConfig.getBaseUrl() + "/api" + request.getRequestURI().replace("/proxy", "");
		LOGGER.info("Proxying request to " + targetApiUrl + " for session " + sessionId + "...");

		AcspUserSession userSession = userSessionRepository.findBySessionId(sessionId);
		if (userSession == null) {
			LOGGER.error("Proxying request to " + targetApiUrl + " for session " + sessionId + " failed because session not found!");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, "Session " + userSession.getSessionToken());
		headers.set("X-Session-ID", sessionId);

		Mono<byte[]> requestBodyMono = Mono.justOrEmpty(request.getInputStream().readAllBytes());

		MultiValueMap<String, String> cookieList = new LinkedMultiValueMap<>();
		Arrays.stream(request.getCookies()).forEach(cookie -> cookieList.add(cookie.getName(), cookie.getValue()));

		ResponseEntity<byte[]> firstExchange = exchangeRequest(method, targetApiUrl, headers, requestBodyMono, cookieList);

		if (!firstExchange.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()))) {
			LOGGER.success("Proxying request to " + targetApiUrl + " for session " + sessionId + " successful at first exchange with status " + firstExchange.getStatusCode() + "!");
			return firstExchange;
		}

		LOGGER.info("Proxying request to " + targetApiUrl + " for session " + sessionId + " failed at first exchange, trying again with new tokens...");

		UserSessionRefreshDto userSessionRefreshDto = userSessionService.refreshUserSession(sessionId);
		if (userSessionRefreshDto == null) {
			LOGGER.error("Proxying request to " + targetApiUrl + " for session " + sessionId + " failed because session refresh failed!");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		headers.set(HttpHeaders.AUTHORIZATION, "Session " + userSessionRefreshDto.getSessionToken());
		headers.set("X-Session-ID", sessionId);

		ResponseEntity<byte[]> secondExchange = exchangeRequest(method, targetApiUrl, headers, requestBodyMono, cookieList);

		if (!secondExchange.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()))) {
			LOGGER.success("Proxying request to " + targetApiUrl + " for session " + sessionId + " successful at second exchange with status " + secondExchange.getStatusCode() + "!");
			return secondExchange;
		}

		LOGGER.error("Proxying request to " + targetApiUrl + " for session " + sessionId + " failed at second exchange!");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	}

	public ResponseEntity<byte[]> exchangeRequest(
			HttpMethod method,
			String targetApiUrl,
			HttpHeaders headers,
			Mono<byte[]> requestBodyMono,
			MultiValueMap<String, String> cookieList
	) {
		return selfClient.method(method)
				.uri(targetApiUrl)
				.headers(httpHeaders -> httpHeaders.addAll(headers))
				.body(requestBodyMono, byte[].class)
				.cookies(cookies -> cookies.putAll(cookieList))
				.exchangeToMono(response -> {
					HttpStatusCode statusCode = response.statusCode();
					HttpHeaders responseHeaders = response.headers().asHttpHeaders();
					return response.bodyToMono(byte[].class)
							.map(body -> new ResponseEntity<>(body, responseHeaders, statusCode))
							.switchIfEmpty(Mono.just(new ResponseEntity<>(responseHeaders, statusCode)));
				}).block();
	}
}