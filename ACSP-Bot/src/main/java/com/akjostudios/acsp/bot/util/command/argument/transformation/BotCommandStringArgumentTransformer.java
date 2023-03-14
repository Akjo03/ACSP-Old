package com.akjostudios.acsp.bot.util.command.argument.transformation;

import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import com.akjostudios.acsp.bot.constants.BotCommandArgumentTypes;
import com.akjostudios.acsp.bot.util.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentConverter;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentConverterProvider;
import com.akjostudios.acsp.bot.util.command.argument.validation.BotCommandStringArgumentValidator;
import io.github.akjo03.lib.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BotCommandStringArgumentTransformer extends BotCommandArgumentTransformer<String> {
	protected BotCommandStringArgumentTransformer(
			String commandName,
			BotConfigCommandArgument<String> argumentDefinition,
			String argumentValue
	) { super(commandName, argumentDefinition, argumentValue); }

	@Contract("_, _, _ -> new")
	public static @NotNull BotCommandStringArgumentTransformer of(
			String commandName,
			BotConfigCommandArgument<String> argumentDefinition,
			String argumentValue
	) {
		return new BotCommandStringArgumentTransformer(
				commandName,
				argumentDefinition,
				argumentValue
		);
	}

	@Override
	public Result<BotCommandArgument<String>> transform() {
		if (!checkIfRequired()) {
			// TODO: Handle error
		}

		BotConfigCommandArgumentData<String> argumentData = getArgumentData();
		BotCommandArgumentConverter<String> converter = BotCommandArgumentConverterProvider.STRING.provide();
		String convertedValue = converter.convertForward(argumentValue);

		if ((convertedValue == null || convertedValue.isEmpty()) && argumentData.getDefaultValue() != null) {
			convertedValue = argumentData.getDefaultValue();
		}

		return BotCommandStringArgumentValidator.of(argumentData).validate(convertedValue)
				.ifError(e -> {
					// TODO: Handle error
				}).map(validatedValue -> BotCommandArgument.of(
						argumentDefinition.getName(),
						argumentDefinition.getDescription(),
						validatedValue,
						argumentData,
						BotCommandArgumentTypes.STRING
				));
	}
}