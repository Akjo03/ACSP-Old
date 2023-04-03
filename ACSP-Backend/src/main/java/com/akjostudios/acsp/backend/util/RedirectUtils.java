package com.akjostudios.acsp.backend.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

@UtilityClass
public class RedirectUtils {
	public ResponseEntity<String> getRedirectResponse(String url) {
		HttpHeaders redirectHeaders = new HttpHeaders();
		redirectHeaders.setLocation(URI.create(url));
		return ResponseEntity.status(HttpStatus.SEE_OTHER).headers(redirectHeaders).build();
	}
}