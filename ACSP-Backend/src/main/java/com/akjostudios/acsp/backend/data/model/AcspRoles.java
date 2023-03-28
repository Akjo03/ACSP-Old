package com.akjostudios.acsp.backend.data.model;

import lombok.Getter;

@Getter
public enum AcspRoles {
	USER("ACSP_ROLE_USER");

	private final String role;

	AcspRoles(String role) {
		this.role = role;
	}
}