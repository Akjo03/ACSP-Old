package com.akjostudios.acsp.backend.model;

import lombok.Getter;

@Getter
public enum AcspRoles {
	USER("ACSP_ROLE_USER");

	private final String role;

	AcspRoles(String role) {
		this.role = role;
	}
}