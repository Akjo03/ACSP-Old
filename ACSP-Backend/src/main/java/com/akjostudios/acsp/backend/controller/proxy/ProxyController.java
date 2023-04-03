package com.akjostudios.acsp.backend.controller.proxy;

import com.akjostudios.acsp.backend.config.ApplicationConfig;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
	@Qualifier("selfClient")
	private final WebClient selfClient;

	private final ApplicationConfig applicationConfig;

	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
	public ResponseEntity<byte[]> proxyRequest(
			@CookieValue("session_id") String sessionId,
			@CookieValue("session_token") String sessionToken,
			@CookieValue("refresh_token") String refreshToken,
			HttpMethod method,
			HttpServletRequest request
	) throws IOException {
		String targetApiUrl = applicationConfig.getBaseUrl() + "/api" + request.getRequestURI().replace("/proxy", "");

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, "Session " + sessionToken);
		headers.set("X-Session-ID", sessionId);

		Mono<byte[]> requestBodyMono = Mono.justOrEmpty(request.getInputStream().readAllBytes());

		MultiValueMap<String, String> cookieList = new LinkedMultiValueMap<>();
		Arrays.stream(request.getCookies()).forEach(cookie -> cookieList.add(cookie.getName(), cookie.getValue()));

		return executeRequest(method, targetApiUrl, headers, requestBodyMono, cookieList);
	}

	public ResponseEntity<byte[]> executeRequest(
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
							.map(body -> new ResponseEntity<>(body, responseHeaders, statusCode));
				}).block();
	}
}