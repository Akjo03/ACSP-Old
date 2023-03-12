package com.akjostudios.acsp.bot.util.command.argument;

import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import com.akjostudios.acsp.bot.constants.BotCommandArgumentTypes;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class BotCommandArgument<T> {
	private final String name;
	private final String description;
	private final T value;
	private final BotConfigCommandArgumentData<T> data;
	private final BotCommandArgumentTypes type;

	private BotCommandArgument(String name, String description, T value, BotConfigCommandArgumentData<T> data, BotCommandArgumentTypes type) {
		this.name = name;
		this.description = description;
		this.value = value;
		this.data = data;
		this.type = type;
	}

	public static <T> @NotNull BotCommandArgument<T> of(String name, String description, T value, BotConfigCommandArgumentData<T> data, BotCommandArgumentTypes type) {
		return new BotCommandArgument<>(name, description, value, data, type);
	}
}