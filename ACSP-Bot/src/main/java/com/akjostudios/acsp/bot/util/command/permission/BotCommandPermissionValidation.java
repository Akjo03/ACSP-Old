package com.akjostudios.acsp.bot.util.command.permission;

import com.akjostudios.acsp.bot.services.bot.BotStringsService;
import lombok.Getter;

import java.util.Optional;

@Getter
public class BotCommandPermissionValidation {
	private final boolean isAllowed;
	private final String reasonLabel;

	private BotCommandPermissionValidation(boolean isAllowed, String reasonLabel) {
		this.isAllowed = isAllowed;
		this.reasonLabel = reasonLabel;
	}

	public static BotCommandPermissionValidation allowed() {
		return new BotCommandPermissionValidation(true, null);
	}

	public static BotCommandPermissionValidation denied(String reasonLabel) {
		return new BotCommandPermissionValidation(false, reasonLabel);
	}

	public String getReason(BotStringsService botStringsService) {
		return botStringsService.getString(reasonLabel, Optional.empty());
	}
}