package com.akjostudios.acsp.bot.constants;

public enum BotDeployMode {
	LOCAL,
	PROD;

	@SuppressWarnings("DuplicateBranchesInSwitch")
	public static BotDeployMode getDeployMode(String deployMode) {
		if (deployMode == null || deployMode.isEmpty()) {
			return LOCAL;
		}

		return switch (deployMode) {
			case "LOCAL" -> LOCAL;
			case "PROD" -> PROD;
			default -> LOCAL;
		};
	}
}