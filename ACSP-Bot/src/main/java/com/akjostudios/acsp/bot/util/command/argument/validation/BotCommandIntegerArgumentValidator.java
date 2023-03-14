package com.akjostudios.acsp.bot.util.command.argument.validation;

import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import io.github.akjo03.lib.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BotCommandIntegerArgumentValidator extends BotCommandArgumentValidator<Integer> {
	protected BotCommandIntegerArgumentValidator(BotConfigCommandArgumentData<Integer> validationData) {
		super(validationData);
	}

	@Contract("_ -> new")
	public static @NotNull BotCommandIntegerArgumentValidator of(BotConfigCommandArgumentData<Integer> validationData) {
		return new BotCommandIntegerArgumentValidator(validationData);
	}

	@Override
	public Result<Integer> validate(Integer value) {
		return null;
	}
}
