package com.akjostudios.acsp.bot.constants;

import lombok.Getter;

import java.util.Arrays;
import java.util.Locale;

@Getter
public enum BotLanguages {
	ENGLISH("en", Locale.ENGLISH),
	GERMAN("de", Locale.GERMAN);

	private final String code;
	private final Locale locale;

	BotLanguages(String code, Locale locale) {
		this.code = code;
		this.locale = locale;
	}

	public static BotLanguages fromCode(String code) {
		return Arrays.stream(values())
				.filter(lang -> lang.getCode().equals(code))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid language code: " + code));
	}
}