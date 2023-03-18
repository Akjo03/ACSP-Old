package com.akjostudios.acsp.bot.util.command.argument.validation;

import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import com.akjostudios.acsp.bot.services.BotConfigService;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import io.github.akjo03.lib.result.Result;

public abstract class BotCommandArgumentValidator<T, D extends BotConfigCommandArgumentData<T>> {
	protected final D validationData;
	protected final String commandName;
	protected final String argumentName;

	protected DiscordMessageService discordMessageService;
	protected BotConfigService botConfigService;

	protected BotCommandArgumentValidator(D validationData, String commandName, String argumentName) {
		this.validationData = validationData;
		this.commandName = commandName;
		this.argumentName = argumentName;
	}

	protected void setupServices(DiscordMessageService discordMessageService, BotConfigService botConfigService) {
		this.discordMessageService = discordMessageService;
		this.botConfigService = botConfigService;
	}

	public abstract Result<Void> validate(T value);
}