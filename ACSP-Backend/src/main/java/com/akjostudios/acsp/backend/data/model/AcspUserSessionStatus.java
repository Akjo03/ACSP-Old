package com.akjostudios.acsp.backend.data.model;

import lombok.Getter;

@Getter
public enum AcspUserSessionStatus {
	ONBOARDING("onboarding"),
	ACTIVE("active"),
	INACTIVE("inactive");

	private final String status;

	AcspUserSessionStatus(String status) {
		this.status = status;
	}
}