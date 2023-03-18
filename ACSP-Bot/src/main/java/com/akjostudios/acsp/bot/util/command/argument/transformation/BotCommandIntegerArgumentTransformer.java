package com.akjostudios.acsp.bot.util.command.argument.transformation;

import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentIntegerData;
import com.akjostudios.acsp.bot.constants.BotCommandArgumentTypes;
import com.akjostudios.acsp.bot.services.BotConfigService;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.util.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentConverter;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentConverterProvider;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentIntegerConverter;
import com.akjostudios.acsp.bot.util.command.argument.validation.BotCommandIntegerArgumentValidator;
import io.github.akjo03.lib.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BotCommandIntegerArgumentTransformer extends BotCommandArgumentTransformer<Integer, BotConfigCommandArgumentIntegerData> {
	protected BotCommandIntegerArgumentTransformer(
			String commandName,
			BotConfigCommandArgument<Integer> argumentDefinition,
			String argumentValue
	) { super(commandName, argumentDefinition, argumentValue); }

	@Contract("_, _, _, _, _ -> new")
	public static @NotNull BotCommandIntegerArgumentTransformer of(
			String commandName,
			BotConfigCommandArgument<Integer> argumentDefinition,
			String argumentValue,
			DiscordMessageService discordMessageService,
			BotConfigService botConfigService
	) {
		BotCommandIntegerArgumentTransformer transformer = new BotCommandIntegerArgumentTransformer(
				commandName,
				argumentDefinition,
				argumentValue
		);
		transformer.setupServices(discordMessageService, botConfigService);
		return transformer;
	}

	@Override
	public Result<BotCommandArgument<Integer>> transform() {
		if (checkIfOptional()) {
			// TODO: Handle error
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
				botConfigService
		).validate(convertedValue)
				.ifError(e -> {
					// TODO: Handle error
				});

		return validationResult.isError() ? Result.fail(validationResult.getError()) : Result.success(BotCommandArgument.of(
				argumentDefinition.getName(),
				argumentDefinition.getDescription(),
				convertedValue,
				argumentData,
				BotCommandArgumentTypes.INTEGER
		));
	}
}