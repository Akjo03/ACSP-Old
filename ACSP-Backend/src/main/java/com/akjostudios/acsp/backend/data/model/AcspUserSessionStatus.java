package com.akjostudios.acsp.backend.data.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum AcspUserSessionStatus {
	ONBOARDING("onboarding"),
	ACTIVE("active"),
	INACTIVE("inactive");

	private final String status;

	AcspUserSessionStatus(String status) {
		this.status = status;
	}

	public static AcspUserSessionStatus fromString(String status) {
		return Arrays.stream(AcspUserSessionStatus.values())
				.filter(s -> s.getStatus().equalsIgnoreCase(status))
				.findFirst()
				.orElse(null);
	}
}