package com.akjostudios.acsp.backend.services.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
	public String getInvalidStateMessage() {
		return "The auth system is either overloaded or the login request is invalid."
				+ "<br /><br />"
				+ "Das Authentifizierungssystem ist entweder überlastet oder die Login-Anfrage ist ungültig.";
	}

	public ResponseEntity<String> getActiveSessionResponse() {
		return ResponseEntity.ok("You are already logged in.");
	}

	public ResponseEntity<String> getLoginSuccessResponse() {
		return ResponseEntity.ok("You have successfully logged in.");
	}
}