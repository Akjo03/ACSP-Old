package com.akjostudios.acsp.bot.constants;

public enum DeployMode {
	LOCAL,
	PROD;

	@SuppressWarnings("DuplicateBranchesInSwitch")
	public static DeployMode getDeployMode(String deployMode) {
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