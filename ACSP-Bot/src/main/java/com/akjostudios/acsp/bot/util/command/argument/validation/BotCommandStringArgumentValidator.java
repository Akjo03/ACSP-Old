package com.akjostudios.acsp.bot.util.command.argument.validation;

import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentStringData;
import com.akjostudios.acsp.bot.services.bot.BotConfigService;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import com.akjostudios.acsp.bot.util.exception.AcspBotCommandArgumentParseException;
import com.akjostudios.acsp.bot.util.exception.AcspBotConfigException;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import io.github.akjo03.lib.result.Result;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

public class BotCommandStringArgumentValidator extends BotCommandArgumentValidator<String, BotConfigCommandArgumentStringData> {
	private static final Logger LOGGER = LoggerManager.getLogger(BotCommandStringArgumentValidator.class);

	protected BotCommandStringArgumentValidator(
			BotConfigCommandArgumentStringData validationData,
			String commandName,
			String argumentName,
			boolean isRequired
	) { super(validationData, commandName, argumentName, isRequired); }

	@Contract("_, _, _, _, _, _, _ -> new")
	public static @NotNull BotCommandStringArgumentValidator of(
			BotConfigCommandArgumentStringData validationData,
			String commandName,
			String argumentName,
			boolean isRequired,
			DiscordMessageService discordMessageService,
			BotConfigService botConfigService,
			ErrorMessageService errorMessageService
	) {
		BotCommandStringArgumentValidator validator = new BotCommandStringArgumentValidator(validationData, commandName, argumentName, isRequired);
		validator.setupServices(discordMessageService, botConfigService, errorMessageService);
		return validator;
	}

	@Override
	public Result<Void> validate(String value, MessageReceivedEvent event) {
		if (value == null) {
			return isRequired ? Result.fail(new AcspBotCommandArgumentParseException(
					argumentName,
					"errors.command_argument_parsing_report.fields.reason.required_missing",
					List.of(),
					null,
					discordMessageService,
					botConfigService
			)) : Result.empty();
		}

		if (value.length() < validationData.getMinLength()) {
			return Result.fail(new AcspBotCommandArgumentParseException(
					argumentName,
					"errors.command_argument_parsing_report.fields.reason.string.too_short",
					List.of(
							String.valueOf(validationData.getMinLength())
					),
					null,
					discordMessageService,
					botConfigService
			));
		}
		if (value.length() > validationData.getMaxLength()) {
			return Result.fail(new AcspBotCommandArgumentParseException(
					argumentName,
					"errors.command_argument_parsing_report.fields.reason.string.too_long",
					List.of(
							String.valueOf(validationData.getMaxLength())
					),
					null,
					discordMessageService,
					botConfigService
			));
		}

		Pattern regexPattern;
		try { regexPattern = Pattern.compile(validationData.getRegex()); } catch (Exception e) {
			return Result.fail(new AcspBotConfigException(
					"Failed to parse regex pattern for argument \"" + argumentName + "\" of command \"" + commandName + "\"!", e,
					discordMessageService, errorMessageService, LOGGER
			));
		}
		if (!regexPattern.matcher(value).find()) {
			return Result.fail(new AcspBotCommandArgumentParseException(
					argumentName,
					"errors.command_argument_parsing_report.fields.reason.string.regex",
					List.of(
							validationData.getRegex(),
							event.getJumpUrl(),
							validationData.getRegexDescription()
					),
					null,
					discordMessageService,
					botConfigService
			));
		}

		return Result.empty();
	}
}
