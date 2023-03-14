package com.akjostudios.acsp.bot.util.command.argument.transformation;

import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import com.akjostudios.acsp.bot.util.command.argument.BotCommandArgument;
import io.github.akjo03.lib.result.Result;

public abstract class BotCommandArgumentTransformer<T> {
	protected final BotConfigCommandArgument<?> argumentDefinition;
	protected final String commandName;
	protected String argumentValue;

	protected BotCommandArgumentTransformer(
			String commandName,
			BotConfigCommandArgument<T> argumentDefinition,
			String argumentValue
	) {
		this.commandName = commandName;
		this.argumentDefinition = argumentDefinition;
		this.argumentValue = argumentValue;
	}

	@SuppressWarnings("unused")
	protected abstract Result<BotCommandArgument<T>> transform();

	@SuppressWarnings("unchecked")
	protected BotConfigCommandArgumentData<T> getArgumentData() {
		return (BotConfigCommandArgumentData<T>) argumentDefinition.getData();
	}

	protected boolean checkIfRequired() {
		if (argumentValue == null || argumentValue.isEmpty()) {
			return !argumentDefinition.isRequired();
		}
		return true;
	}
}