package com.akjostudios.acsp.bot.util.command.argument.conversion;

public class BotCommandArgumentIntegerConverter extends BotCommandArgumentConverter<Integer> {
	protected BotCommandArgumentIntegerConverter() {
		super(Integer::parseInt, Object::toString);
	}
}