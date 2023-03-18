package com.akjostudios.acsp.bot.util.command.argument.validation;

import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentStringData;
import com.akjostudios.acsp.bot.services.BotConfigService;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import io.github.akjo03.lib.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class BotCommandStringArgumentValidator extends BotCommandArgumentValidator<String, BotConfigCommandArgumentStringData> {
	private static final Logger LOGGER = LoggerManager.getLogger(BotCommandStringArgumentValidator.class);

	protected BotCommandStringArgumentValidator(
			BotConfigCommandArgumentStringData validationData,
			String commandName,
			String argumentName
	) { super(validationData, commandName, argumentName); }

	@Contract("_, _, _, _, _ -> new")
	public static @NotNull BotCommandStringArgumentValidator of(
			BotConfigCommandArgumentStringData validationData,
			String commandName,
			String argumentName,
			DiscordMessageService discordMessageService,
			BotConfigService botConfigService
	) {
		BotCommandStringArgumentValidator validator = new BotCommandStringArgumentValidator(validationData, commandName, argumentName);
		validator.setupServices(discordMessageService, botConfigService);
		return validator;
	}

	@Override
	public Result<Void> validate(String value) {
		if (value != null) {
			if (value.length() < validationData.getMinLength()) {
				// TODO: Handle validation error
				return null;
			}
			if (value.length() > validationData.getMaxLength()) {
				// TODO: Handle validation error
				return null;
			}
		}

		Pattern regexPattern;
		try { regexPattern = Pattern.compile(validationData.getRegex()); } catch (Exception e) {
			LOGGER.error("Failed to parse regex pattern for argument \"" + argumentName + "\" of command \"" + commandName + "\"!");
			// TODO: Handle validation error
			return null;
		}
		if (!regexPattern.matcher(value).find()) {
			// TODO: Handle validation error
			return null;
		}

		return Result.empty();
	}
}
