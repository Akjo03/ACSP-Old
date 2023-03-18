package com.akjostudios.acsp.bot.util.command.argument.validation;

import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentIntegerData;
import com.akjostudios.acsp.bot.services.BotConfigService;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import com.akjostudios.acsp.bot.util.exception.AcspBotCommandArgumentParseException;
import io.github.akjo03.lib.result.Result;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BotCommandIntegerArgumentValidator extends BotCommandArgumentValidator<Integer, BotConfigCommandArgumentIntegerData> {
	protected BotCommandIntegerArgumentValidator(
			BotConfigCommandArgumentIntegerData validationData,
			String commandName,
			String argumentName
	) { super(validationData, commandName, argumentName); }

	@Contract("_, _, _, _, _, _ -> new")
	public static @NotNull BotCommandIntegerArgumentValidator of(
			BotConfigCommandArgumentIntegerData validationData,
			String commandName,
			String argumentName,
			DiscordMessageService discordMessageService,
			BotConfigService botConfigService,
			ErrorMessageService errorMessageService
	) {
		BotCommandIntegerArgumentValidator validator = new BotCommandIntegerArgumentValidator(validationData, commandName, argumentName);
		validator.setupServices(discordMessageService, botConfigService, errorMessageService);
		return validator;
	}

	@Override
	public Result<Void> validate(Integer value, MessageReceivedEvent event) {
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