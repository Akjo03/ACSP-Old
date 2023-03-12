package com.akjostudios.acsp.bot.constants;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class BotConstants {
	private BotConstants() {}

	public static final DateTimeFormatter DATE_TIME_FORMATTER =
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
					.withZone(ZoneId.of("Europe/Zurich"));
}