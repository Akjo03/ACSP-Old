package com.akjostudios.acsp.bot.util.exception;

import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import io.github.akjo03.lib.logging.Logger;

public class AcspBotConfigException extends AcspBotException {
	public AcspBotConfigException(String message, Throwable cause, DiscordMessageService discordMessageService, ErrorMessageService errorMessageService, Logger logger) {
		super("The bot config is invalid: " + message, cause, discordMessageService, errorMessageService, logger);
	}
}