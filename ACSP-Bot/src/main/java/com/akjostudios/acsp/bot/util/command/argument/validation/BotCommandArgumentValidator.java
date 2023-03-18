package com.akjostudios.acsp.bot.util.command.argument.validation;

import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import com.akjostudios.acsp.bot.services.BotConfigService;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import io.github.akjo03.lib.result.Result;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class BotCommandArgumentValidator<T, D extends BotConfigCommandArgumentData<T>> {
	protected final D validationData;
	protected final String commandName;
	protected final String argumentName;
	protected final boolean isRequired;

	protected DiscordMessageService discordMessageService;
	protected BotConfigService botConfigService;
	protected ErrorMessageService errorMessageService;

	protected BotCommandArgumentValidator(D validationData, String commandName, String argumentName, boolean isRequired) {
		this.validationData = validationData;
		this.commandName = commandName;
		this.argumentName = argumentName;
		this.isRequired = isRequired;
	}

	protected void setupServices(DiscordMessageService discordMessageService, BotConfigService botConfigService, ErrorMessageService errorMessageService) {
		this.discordMessageService = discordMessageService;
		this.botConfigService = botConfigService;
		this.errorMessageService = errorMessageService;
	}

	public abstract Result<Void> validate(T value, MessageReceivedEvent event);
}