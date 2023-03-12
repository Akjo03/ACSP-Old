package com.akjostudios.acsp.bot.util.constants;

import com.akjostudios.acsp.bot.AcspBot;

public interface IdentifiableConstants {
	default long getId() {
		return switch (AcspBot.getBotDeployMode()) {
			case LOCAL -> getLocalId();
			case PROD -> getProdId();
		};
	}

	long getLocalId();
	long getProdId();
}