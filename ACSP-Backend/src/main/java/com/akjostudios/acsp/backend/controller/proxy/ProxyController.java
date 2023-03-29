package com.akjostudios.acsp.backend.controller.proxy;

import com.akjostudios.acsp.backend.config.ApplicationConfig;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/proxy/**")
public class ProxyController {
	private static final Logger LOGGER = LoggerManager.getLogger(ProxyController.class);

	private final WebClient selfClient;

	private final ApplicationConfig applicationConfig;

	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
	public ResponseEntity<byte[]> proxyRequest(@CookieValue("session_token") String sessionToken, HttpMethod method, HttpServletRequest request) throws IOException {
		String targetApiUrl = applicationConfig.getBaseUrl() + "/api" + request.getRequestURI().replace("/proxy", "");
		LOGGER.info("Proxying request to " + targetApiUrl);

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, "Session " + sessionToken);

		Mono<byte[]> requestBodyMono = Mono.justOrEmpty(request.getInputStream().readAllBytes());

		return selfClient.method(method)
				.uri(targetApiUrl)
				.headers(httpHeaders -> httpHeaders.addAll(headers))
				.body(requestBodyMono, byte[].class)
				.exchangeToMono(response -> {
					HttpStatusCode statusCode = response.statusCode();
					HttpHeaders responseHeaders = response.headers().asHttpHeaders();
					return response.bodyToMono(byte[].class)
							.map(body -> new ResponseEntity<>(body, responseHeaders, statusCode));
				}).block();
	}
}