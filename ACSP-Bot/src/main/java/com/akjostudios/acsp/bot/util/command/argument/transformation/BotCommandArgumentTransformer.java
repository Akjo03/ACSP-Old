package com.akjostudios.acsp.bot.util.command.argument.transformation;

import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import com.akjostudios.acsp.bot.services.BotConfigService;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.util.command.argument.BotCommandArgument;
import io.github.akjo03.lib.result.Result;

public abstract class BotCommandArgumentTransformer<T, D extends BotConfigCommandArgumentData<T>> {
	protected final BotConfigCommandArgument<T> argumentDefinition;
	protected final String commandName;
	protected String argumentValue;

	protected DiscordMessageService discordMessageService;
	protected BotConfigService botConfigService;

	protected BotCommandArgumentTransformer(
			String commandName,
			BotConfigCommandArgument<T> argumentDefinition,
			String argumentValue
	) {
		this.commandName = commandName;
		this.argumentDefinition = argumentDefinition;
		this.argumentValue = argumentValue;
	}

	protected void setupServices(DiscordMessageService discordMessageService, BotConfigService botConfigService) {
		this.discordMessageService = discordMessageService;
		this.botConfigService = botConfigService;
	}

	@SuppressWarnings("unused")
	protected abstract Result<BotCommandArgument<T>> transform();

	@SuppressWarnings("unchecked")
	protected D getArgumentData() {
		return (D) argumentDefinition.getData();
	}

	protected boolean checkIfOptional() {
		if (argumentValue == null || argumentValue.isEmpty()) {
			return argumentDefinition.isRequired();
		}
		return false;
	}
}