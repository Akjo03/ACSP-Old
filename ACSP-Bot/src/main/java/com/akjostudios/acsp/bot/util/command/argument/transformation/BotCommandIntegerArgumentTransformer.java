package com.akjostudios.acsp.bot.util.command.argument.transformation;

import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import com.akjostudios.acsp.bot.constants.BotCommandArgumentTypes;
import com.akjostudios.acsp.bot.util.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentConverter;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentConverterProvider;
import com.akjostudios.acsp.bot.util.command.argument.validation.BotCommandIntegerArgumentValidator;
import com.akjostudios.acsp.bot.util.command.argument.validation.BotCommandStringArgumentValidator;
import io.github.akjo03.lib.result.Result;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BotCommandIntegerArgumentTransformer extends BotCommandArgumentTransformer<Integer> {
	protected BotCommandIntegerArgumentTransformer(
			String commandName,
			BotConfigCommandArgument<Integer> argumentDefinition,
			String argumentValue
	) { super(commandName, argumentDefinition, argumentValue); }

	@Contract("_, _, _ -> new")
	public static @NotNull BotCommandIntegerArgumentTransformer of(
			String commandName,
			BotConfigCommandArgument<Integer> argumentDefinition,
			String argumentValue
	) {
		return new BotCommandIntegerArgumentTransformer(
				commandName,
				argumentDefinition,
				argumentValue
		);
	}

	@Override
	public Result<BotCommandArgument<Integer>> transform() {
		if (!checkIfRequired()) {
			// TODO: Handle error
		}

		BotConfigCommandArgumentData<Integer> argumentData = getArgumentData();
		BotCommandArgumentConverter<Integer> converter = BotCommandArgumentConverterProvider.INTEGER.provide();
		Integer convertedValue = converter.convertForward(argumentValue);

		if (convertedValue == null && argumentData.getDefaultValue() != null) {
			convertedValue = argumentData.getDefaultValue();
		}

		return BotCommandIntegerArgumentValidator.of(argumentData).validate(convertedValue)
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