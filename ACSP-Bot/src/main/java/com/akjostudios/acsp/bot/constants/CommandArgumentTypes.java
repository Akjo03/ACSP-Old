package com.akjostudios.acsp.bot.constants;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@SuppressWarnings("unused")
public enum CommandArgumentTypes {
	STRING("STRING", "command.arguments.type.string"),
	INTEGER("INTEGER", "command.arguments.type.integer");

	private final String type;
	private final String translationKey;

	CommandArgumentTypes(String type, String translationKey) {
		this.type = type;
		this.translationKey = translationKey;
	}

	public static @Nullable CommandArgumentTypes fromString(String type) {
		return Arrays.stream(CommandArgumentTypes.values())
				.filter(t -> t.getType().equals(type))
				.findFirst()
				.orElse(null);
	}

	public String getType() {
		return type;
	}

	public String getKey() {
		return translationKey;
	}

	public String getTooltipKey() {
		return translationKey + ".tooltip";
	}
}