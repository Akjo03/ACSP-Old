package com.akjostudios.acsp.backend.model;

import lombok.Getter;

@Getter
public enum AcspUserSessionStatus {
	ONBOARDING("onboarding");

	private final String status;

	AcspUserSessionStatus(String status) {
		this.status = status;
	}
}