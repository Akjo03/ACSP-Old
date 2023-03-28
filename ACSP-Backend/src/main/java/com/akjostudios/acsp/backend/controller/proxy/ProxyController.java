package com.akjostudios.acsp.backend.controller.proxy;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/proxy/**")
public class ProxyController {
	@Qualifier("selfClient")
	private final WebClient selfClient;

	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
	public Mono<ResponseEntity<byte[]>> proxyRequest(@CookieValue("session_id") String sessionId, @CookieValue("session_token") String sessionToken, HttpMethod method, HttpServletRequest request) {
		return Mono.empty();
	}
}