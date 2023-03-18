package com.akjostudios.acsp.bot.util.command.argument.validation;

import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentIntegerData;
import com.akjostudios.acsp.bot.services.BotConfigService;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.util.exception.AcspBotCommandArgumentParseException;
import io.github.akjo03.lib.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BotCommandIntegerArgumentValidator extends BotCommandArgumentValidator<Integer, BotConfigCommandArgumentIntegerData> {
	protected BotCommandIntegerArgumentValidator(
			BotConfigCommandArgumentIntegerData validationData,
			String commandName,
			String argumentName
	) { super(validationData, commandName, argumentName); }

	@Contract("_, _, _, _, _ -> new")
	public static @NotNull BotCommandIntegerArgumentValidator of(
			BotConfigCommandArgumentIntegerData validationData,
			String commandName,
			String argumentName,
			DiscordMessageService discordMessageService,
			BotConfigService botConfigService
	) {
		BotCommandIntegerArgumentValidator validator = new BotCommandIntegerArgumentValidator(validationData, commandName, argumentName);
		validator.setupServices(discordMessageService, botConfigService);
		return validator;
	}

	@Override
	public Result<Void> validate(Integer value) {
		if (value != null) {
			if (value < validationData.getMinValue()) {
				return Result.fail(new AcspBotCommandArgumentParseException(
						argumentName,
						"errors.command_argument_parsing_report.fields.reason.integer.too_small",
						List.of(
								String.valueOf(validationData.getMinValue())
						),
						null,
						discordMessageService,
						botConfigService
				));
			}
			if (value > validationData.getMaxValue()) {
				return Result.fail(new AcspBotCommandArgumentParseException(
						argumentName,
						"errors.command_argument_parsing_report.fields.reason.integer.too_big",
						List.of(
								String.valueOf(validationData.getMaxValue())
						),
						null,
						discordMessageService,
						botConfigService
				));
			}
		}

		return Result.empty();
	}
}
