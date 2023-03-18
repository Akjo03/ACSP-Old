package com.akjostudios.acsp.bot.util.command.argument.transformation;

import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentStringData;
import com.akjostudios.acsp.bot.constants.BotCommandArgumentTypes;
import com.akjostudios.acsp.bot.services.BotConfigService;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.util.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentConverterProvider;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentStringConverter;
import com.akjostudios.acsp.bot.util.command.argument.validation.BotCommandStringArgumentValidator;
import io.github.akjo03.lib.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BotCommandStringArgumentTransformer extends BotCommandArgumentTransformer<String, BotConfigCommandArgumentStringData> {
	protected BotCommandStringArgumentTransformer(
			String commandName,
			BotConfigCommandArgument<String> argumentDefinition,
			String argumentValue
	) { super(commandName, argumentDefinition, argumentValue); }

	@Contract("_, _, _, _, _ -> new")
	public static @NotNull BotCommandStringArgumentTransformer of(
			String commandName,
			BotConfigCommandArgument<String> argumentDefinition,
			String argumentValue,
			DiscordMessageService discordMessageService,
			BotConfigService botConfigService
	) {
		BotCommandStringArgumentTransformer transformer = new BotCommandStringArgumentTransformer(
				commandName,
				argumentDefinition,
				argumentValue
		);
		transformer.setupServices(discordMessageService, botConfigService);
		return transformer;
	}

	@Override
	public Result<BotCommandArgument<String>> transform() {
		if (checkIfOptional()) {
			// TODO: Handle error
		}

		BotConfigCommandArgumentStringData argumentData = getArgumentData();
		BotCommandArgumentStringConverter converter = BotCommandArgumentConverterProvider.STRING.provide();
		String convertedValue = converter.convertForward(argumentValue);

		if (convertedValue == null && argumentData.getDefaultValue() != null) {
			convertedValue = argumentData.getDefaultValue();
		}

		Result<Void> validationResult = BotCommandStringArgumentValidator.of(
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