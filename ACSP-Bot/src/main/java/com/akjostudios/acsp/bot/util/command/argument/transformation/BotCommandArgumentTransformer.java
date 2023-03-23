package com.akjostudios.acsp.bot.util.command.argument.transformation;

import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import com.akjostudios.acsp.bot.constants.BotCommandArgumentTypes;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import com.akjostudios.acsp.bot.services.bot.BotConfigService;
import com.akjostudios.acsp.bot.services.bot.BotStringsService;
import com.akjostudios.acsp.bot.util.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.util.command.argument.conversion.BotCommandArgumentConverter;
import com.akjostudios.acsp.bot.util.exception.AcspBotCommandArgumentParseException;
import io.github.akjo03.lib.result.Result;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class BotCommandArgumentTransformer<T, D extends BotConfigCommandArgumentData<T>> {
	protected final BotConfigCommandArgument<T> argumentDefinition;
	protected final String commandName;
	protected final String argumentValue;

	protected DiscordMessageService discordMessageService;
	protected BotConfigService botConfigService;
	protected ErrorMessageService errorMessageService;
	protected BotStringsService botStringsService;

	protected BotCommandArgumentTransformer(
			String commandName,
			BotConfigCommandArgument<T> argumentDefinition,
			String argumentValue
	) {
		this.commandName = commandName;
		this.argumentDefinition = argumentDefinition;
		this.argumentValue = argumentValue;
	}

	protected void setupServices(
			DiscordMessageService discordMessageService,
			BotConfigService botConfigService,
			ErrorMessageService errorMessageService,
			BotStringsService botStringsService
	) {
		this.discordMessageService = discordMessageService;
		this.botConfigService = botConfigService;
		this.errorMessageService = errorMessageService;
		this.botStringsService = botStringsService;
	}

	@SuppressWarnings("unused")
	protected abstract Result<BotCommandArgument<T>> transform(MessageReceivedEvent event);

	@SuppressWarnings("unchecked")
	protected D getArgumentData() {
		return (D) argumentDefinition.getData();
	}

	protected boolean checkIfRequired() {
		if (argumentValue == null || argumentValue.isEmpty()) {
			return argumentDefinition.isRequired();
		}
		return false;
	}

	protected @Nullable Result<T> convert(BotCommandArgumentConverter<T> converter, MessageReceivedEvent event) {
		if (argumentValue == null) { return null; }

		try {
			return Result.success(converter.convertForward(argumentValue));
		} catch (Exception e) {
			return Result.fail(new AcspBotCommandArgumentParseException(
					argumentDefinition.getName(),
					"errors.command_argument_parsing_report.fields.reason.invalid_type",
					List.of(
							botStringsService.getString(
									Objects.requireNonNull(BotCommandArgumentTypes.fromString(argumentDefinition.getType())).getKey(),
									Optional.empty()
							),
							event.getJumpUrl(),
							botStringsService.getString(
									Objects.requireNonNull(BotCommandArgumentTypes.fromString(argumentDefinition.getType())).getTooltipKey(),
									Optional.empty()
							)
					),
					null,
					discordMessageService, botConfigService
			));
		}
	}
}