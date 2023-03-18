package com.akjostudios.acsp.bot.util.command.argument.transformation;

import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentStringData;
import com.akjostudios.acsp.bot.constants.BotCommandArgumentTypes;
import com.akjostudios.acsp.bot.services.BotConfigService;
import com.akjostudios.acsp.bot.services.BotStringsService;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import com.akjostudios.acsp.bot.util.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentConverterProvider;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentStringConverter;
import com.akjostudios.acsp.bot.util.command.argument.validation.BotCommandStringArgumentValidator;
import com.akjostudios.acsp.bot.util.exception.AcspBotCommandArgumentParseException;
import io.github.akjo03.lib.result.Result;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BotCommandStringArgumentTransformer extends BotCommandArgumentTransformer<String, BotConfigCommandArgumentStringData> {
	protected BotCommandStringArgumentTransformer(
			String commandName,
			BotConfigCommandArgument<String> argumentDefinition,
			String argumentValue
	) { super(commandName, argumentDefinition, argumentValue); }

	@Contract("_, _, _, _, _, _, _ -> new")
	public static @NotNull BotCommandStringArgumentTransformer of(
			String commandName,
			BotConfigCommandArgument<String> argumentDefinition,
			String argumentValue,
			DiscordMessageService discordMessageService,
			BotConfigService botConfigService,
			ErrorMessageService errorMessageService,
			BotStringsService botStringsService
	) {
		BotCommandStringArgumentTransformer transformer = new BotCommandStringArgumentTransformer(
				commandName,
				argumentDefinition,
				argumentValue
		);
		transformer.setupServices(discordMessageService, botConfigService, errorMessageService, botStringsService);
		return transformer;
	}

	@Override
	@SuppressWarnings("DuplicatedCode")
	public Result<BotCommandArgument<String>> transform(MessageReceivedEvent event) {
		if (checkIfRequired()) {
			return Result.fail(new AcspBotCommandArgumentParseException(
					argumentDefinition.getName(),
					"errors.command_argument_parsing_report.fields.reason.required_missing",
					List.of(),
					null,
					discordMessageService, botConfigService
			));
		}

		BotConfigCommandArgumentStringData argumentData = getArgumentData();
		BotCommandArgumentStringConverter converter = BotCommandArgumentConverterProvider.STRING.provide();
		Result<String> convertedValueResult = convert(converter, event);
		String convertedValue = null;

		if (convertedValueResult != null) {
			if (convertedValueResult.isError()) {
				return Result.fail(convertedValueResult.getError());
			}
			convertedValue = convertedValueResult.get();
		}

		if (convertedValue == null && argumentData.getDefaultValue() != null) {
			convertedValue = argumentData.getDefaultValue();
		}

		Result<Void> validationResult = BotCommandStringArgumentValidator.of(
				argumentData,
				commandName,
				argumentDefinition.getName(),
				argumentDefinition.isRequired(),
				discordMessageService,
				botConfigService,
				errorMessageService
		).validate(convertedValue, event);

		return validationResult.isError() ? Result.fail(validationResult.getError()) : Result.success(BotCommandArgument.of(
				argumentDefinition.getName(),
				argumentDefinition.getDescription(),
				convertedValue,
				argumentData,
				BotCommandArgumentTypes.STRING
		));
	}
}