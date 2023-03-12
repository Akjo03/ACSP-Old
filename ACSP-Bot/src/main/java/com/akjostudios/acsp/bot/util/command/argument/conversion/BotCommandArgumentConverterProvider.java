package com.akjostudios.acsp.bot.util.command.argument.conversion;

@FunctionalInterface
public interface BotCommandArgumentConverterProvider<T, C extends BotCommandArgumentConverter<T>> {
	BotCommandArgumentConverterProvider<String, BotCommandArgumentStringConverter> STRING = BotCommandArgumentStringConverter::new;
	BotCommandArgumentConverterProvider<Integer, BotCommandArgumentIntegerConverter> INTEGER = BotCommandArgumentIntegerConverter::new;

	C provide();
}