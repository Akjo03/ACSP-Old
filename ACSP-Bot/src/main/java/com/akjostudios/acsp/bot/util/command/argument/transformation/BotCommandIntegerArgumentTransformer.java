package com.akjostudios.acsp.bot.util.command.argument.transformation;

import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentIntegerData;
import com.akjostudios.acsp.bot.constants.BotCommandArgumentTypes;
import com.akjostudios.acsp.bot.services.BotConfigService;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import com.akjostudios.acsp.bot.util.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentConverterProvider;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentIntegerConverter;
import com.akjostudios.acsp.bot.util.command.argument.validation.BotCommandIntegerArgumentValidator;
import com.akjostudios.acsp.bot.util.exception.AcspBotCommandArgumentParseException;
import io.github.akjo03.lib.result.Result;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BotCommandIntegerArgumentTransformer extends BotCommandArgumentTransformer<Integer, BotConfigCommandArgumentIntegerData> {
	protected BotCommandIntegerArgumentTransformer(
			String commandName,
			BotConfigCommandArgument<Integer> argumentDefinition,
			String argumentValue
	) { super(commandName, argumentDefinition, argumentValue); }

	@Contract("_, _, _, _, _, _ -> new")
	public static @NotNull BotCommandIntegerArgumentTransformer of(
			String commandName,
			BotConfigCommandArgument<Integer> argumentDefinition,
			String argumentValue,
			DiscordMessageService discordMessageService,
			BotConfigService botConfigService,
			ErrorMessageService errorMessageService
	) {
		BotCommandIntegerArgumentTransformer transformer = new BotCommandIntegerArgumentTransformer(
				commandName,
				argumentDefinition,
				argumentValue
		);
		transformer.setupServices(discordMessageService, botConfigService, errorMessageService);
		return transformer;
	}

	@Override
	public Result<BotCommandArgument<Integer>> transform(MessageReceivedEvent event) {
		if (checkIfRequired()) {
			return Result.fail(new AcspBotCommandArgumentParseException(
					argumentDefinition.getName(),
					"errors.command_argument_parsing_report.fields.reason.required_missing",
					List.of(),
					null,
					discordMessageService, botConfigService
			));
		}

		BotConfigCommandArgumentIntegerData argumentData = getArgumentData();
		BotCommandArgumentIntegerConverter converter = BotCommandArgumentConverterProvider.INTEGER.provide();
		Integer convertedValue = converter.convertForward(argumentValue);

		if (convertedValue == null && argumentData.getDefaultValue() != null) {
			convertedValue = argumentData.getDefaultValue();
		}

		Result<Void> validationResult = BotCommandIntegerArgumentValidator.of(
				argumentData,
				commandName,
				argumentDefinition.getName(),
				discordMessageService,
				botConfigService,
				errorMessageService
		).validate(convertedValue, event);

		return validationResult.isError() ? Result.fail(validationResult.getError()) : Result.success(BotCommandArgument.of(
				argumentDefinition.getName(),
				argumentDefinition.getDescription(),
				convertedValue,
				argumentData,
				BotCommandArgumentTypes.INTEGER
		));
	}
}