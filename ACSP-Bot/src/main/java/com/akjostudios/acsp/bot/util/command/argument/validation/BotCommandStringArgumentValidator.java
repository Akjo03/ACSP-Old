package com.akjostudios.acsp.bot.util.command.argument.validation;

import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import io.github.akjo03.lib.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BotCommandStringArgumentValidator extends BotCommandArgumentValidator<String> {
	protected BotCommandStringArgumentValidator(BotConfigCommandArgumentData<String> validationData) {
		super(validationData);
	}

	@Contract("_ -> new")
	public static @NotNull BotCommandStringArgumentValidator of(BotConfigCommandArgumentData<String> validationData) {
		return new BotCommandStringArgumentValidator(validationData);
	}

	@Override
	public Result<String> validate(String value) {
		return null;
	}
}
