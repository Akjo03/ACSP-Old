package com.akjostudios.acsp.bot.util.command.argument;

import com.akjostudios.acsp.bot.constants.BotCommandArgumentTypes;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class BotCommandArguments {
	private static final Logger LOGGER = LoggerManager.getLogger(BotCommandArguments.class);

	private final String commandName;
	private final List<BotCommandArgument<?>> arguments;
	private final String subcommand;
	private final List<BotCommandArgument<?>> subcommandArguments;

	private BotCommandArguments(String commandName, List<BotCommandArgument<?>> arguments) {
		this.commandName = commandName;
		this.arguments = arguments;
		this.subcommand = null;
		this.subcommandArguments = null;
	}

	private BotCommandArguments(String commandName, List<BotCommandArgument<?>> arguments, String subcommand, List<BotCommandArgument<?>> subcommandArguments) {
		this.commandName = commandName;
		this.arguments = arguments;
		this.subcommand = subcommand;
		this.subcommandArguments = subcommandArguments;
	}

	@Contract(value = "_, _ -> new", pure = true)
	public static @NotNull BotCommandArguments of(String commandName, List<BotCommandArgument<?>> arguments) {
		return new BotCommandArguments(commandName, arguments);
	}

	@Contract(value = "_, _, _, _ -> new", pure = true)
	public static @NotNull BotCommandArguments of(String commandName, List<BotCommandArgument<?>> arguments, String subcommand, List<BotCommandArgument<?>> subcommandArguments) {
		return new BotCommandArguments(commandName, arguments, subcommand, subcommandArguments);
	}

	@SuppressWarnings("unchecked")
	public <T> @Nullable T getCommandArgument(String argumentName, BotCommandArgumentTypes type) {
		BotCommandArgument<?> commandArgument = arguments.stream()
				.filter(argument -> argument.getName().equals(argumentName))
				.findFirst()
				.orElse(null);
		if (commandArgument == null) {
			LOGGER.error("Failed to find command argument with name \"" + argumentName + "\" in command \"" + commandName + "\"!");
			return null;
		}
		if (!commandArgument.getType().equals(type)) {
			LOGGER.error("Command argument with name \"" + argumentName + "\" in command \"" + commandName + "\" is not of type " + type + "!");
			return null;
		}
		return (T) commandArgument.getValue();
	}

	@SuppressWarnings("unchecked")
	public <T> @Nullable T getSubcommandArgument(String argumentName, BotCommandArgumentTypes type) {
		if (subcommand == null || subcommandArguments == null) {
			LOGGER.error("No subcommand arguments found in command \"" + commandName + "\"!");
			return null;
		}

		BotCommandArgument<?> commandArgument = subcommandArguments.stream()
				.filter(argument -> argument.getName().equals(argumentName))
				.findFirst()
				.orElse(null);
		if (commandArgument == null) {
			LOGGER.error("Failed to find subcommand argument with name \"" + argumentName + "\" in command \"" + commandName + "\" and subcommand \"" + subcommand + "\"!");
			return null;
		}
		if (!commandArgument.getType().equals(type)) {
			LOGGER.error("Subcommand argument with name \"" + argumentName + "\" in command \"" + commandName + "\" and subcommand \"" + subcommand + "\" is not of type " + type + "!");
			return null;
		}
		return (T) commandArgument.getValue();
	}
}