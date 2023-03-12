package com.akjostudios.acsp.bot.util.command.argument.conversion;

import io.github.akjo03.lib.converter.Converter;

import java.util.function.Function;

public abstract class BotCommandArgumentConverter<T> extends Converter<String, T> {
	protected BotCommandArgumentConverter(Function<String, T> forward, Function<T, String> backward) {
		super(forward, backward);
	}
}