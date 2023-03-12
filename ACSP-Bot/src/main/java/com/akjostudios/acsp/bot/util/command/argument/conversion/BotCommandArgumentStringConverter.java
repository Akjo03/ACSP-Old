package com.akjostudios.acsp.bot.util.command.argument.conversion;

import java.util.function.Function;

public class BotCommandArgumentStringConverter extends BotCommandArgumentConverter<String> {
	protected BotCommandArgumentStringConverter() {
		super(Function.identity(), Function.identity());
	}
}