package com.akjostudios.acsp.bot.util.command.argument.validation;

import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import io.github.akjo03.lib.result.Result;

public abstract class BotCommandArgumentValidator<T> {
	protected final BotConfigCommandArgumentData<T> validationData;

	protected BotCommandArgumentValidator(BotConfigCommandArgumentData<T> validationData) {
		this.validationData = validationData;
	}

	public abstract Result<T> validate(T value);
}