package com.akjostudios.acsp.bot.util.command.argument;

import com.akjostudios.acsp.bot.config.bot.command.BotConfigCommand;
import com.akjostudios.acsp.bot.config.bot.command.BotConfigSubcommand;
import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.message.BotConfigMessage;
import com.akjostudios.acsp.bot.constants.BotLanguages;
import com.akjostudios.acsp.bot.services.BotConfigService;
import com.akjostudios.acsp.bot.services.CommandHelperService;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import com.google.common.collect.Lists;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BotCommandArgumentParser {
	private static final Logger LOGGER = LoggerManager.getLogger(BotCommandArgumentParser.class);

	private final String commandName;
	private final BotConfigCommand commandDefinition;
	private final List<String> args;
	private final String subcommand;
	private final List<String> subcommandArgs;

	private final MessageReceivedEvent event;

	private BotConfigService botConfigService;
	private DiscordMessageService discordMessageService;
	private ErrorMessageService errorMessageService;
	private CommandHelperService commandHelperService;

	public BotCommandArgumentParser(String commandName, BotConfigCommand commandDefinition, List<String> args, MessageReceivedEvent event) {
		this.commandName = commandName;
		this.commandDefinition = commandDefinition;
		this.args = args;
		this.subcommand = null;
		this.subcommandArgs = null;
		this.event = event;
	}

	public BotCommandArgumentParser(String commandName, BotConfigCommand commandDefinition, List<String> args, String subcommand, List<String> subcommandArgs, MessageReceivedEvent event) {
		this.commandName = commandName;
		this.commandDefinition = commandDefinition;
		this.args = args;
		this.subcommand = subcommand;
		this.subcommandArgs = subcommandArgs;
		this.event = event;
	}

	public void setupServices(
			BotConfigService botConfigService,
			DiscordMessageService discordMessageService,
			ErrorMessageService errorMessageService,
			CommandHelperService commandHelperService
	) {
		this.botConfigService = botConfigService;
		this.discordMessageService = discordMessageService;
		this.errorMessageService = errorMessageService;
		this.commandHelperService = commandHelperService;
	}

	public BotCommandArguments parse() {
		Map<String, String> suppliedArgs = getSuppliedArguments(args, commandDefinition.getArguments(), false);
		if (suppliedArgs == null) {
			return null;
		}
		if (checkRequiredArguments(suppliedArgs, commandDefinition.getArguments(), false)) {
			return null;
		}
		List<BotCommandArgument<?>> commandArgs = parseArguments(suppliedArgs, commandDefinition.getArguments(), false);

		BotConfigSubcommand subcommandDefinition = commandHelperService.getSubcommandDefinitionFromCommand(commandDefinition, subcommand);
		if (subcommandDefinition == null) {
			return null;
		}
		Map<String, String> suppliedSubcommandArgs = getSuppliedArguments(args, subcommandDefinition.getArguments(), true);
		if (suppliedSubcommandArgs == null) {
			return null;
		}
		if (checkRequiredArguments(suppliedSubcommandArgs, subcommandDefinition.getArguments(), true)) {
			return null;
		}
		List<BotCommandArgument<?>> subcommandArgs = parseArguments(suppliedSubcommandArgs, subcommandDefinition.getArguments(), true);

		return subcommand != null
				? BotCommandArguments.of(commandName, commandArgs, subcommand, subcommandArgs)
				: BotCommandArguments.of(commandName, commandArgs);
	}

	private @Nullable Map<String, String> getSuppliedArguments(
			List<String> argsList,
			List<BotConfigCommandArgument<?>> argumentDefinitions,
			boolean isSubcommand
	) {
		return null;
	}

	private boolean checkRequiredArguments(
			Map<String, String> suppliedArguments,
			List<BotConfigCommandArgument<?>> argumentDefinitions,
			boolean isSubcommand
	) {
		return false;
	}

	private @Nullable List<BotCommandArgument<?>> parseArguments(
			Map<String, String> suppliedArguments,
			List<BotConfigCommandArgument<?>> argumentDefinitions,
			boolean isSubcommand
	) {
		return null;
	}

	private @Nullable Map.Entry<String, String> getNamedArgument(
			String arg,
			Map<String, String> existingArguments,
			List<BotConfigCommandArgument<?>> argumentDefinitions,
			boolean isSubcommand
	) {
		String[] split = arg.split("=", 2);
		String argName = split[0];
		String argValue = split[1];

		if (argumentDefinitions.stream().noneMatch(argDef -> argDef.getName().equals(argName))) {
			event.getChannel().sendMessage(discordMessageService.createMessage(getErrorMessage(
					"errors.command_unknown_argument",
					"errors.subcommand_unknown_argument",
					Lists.newArrayList(),
					Lists.newArrayList(
							argName,
							commandName
					),
					Lists.newArrayList(),
					Lists.newArrayList(
							argName,
							subcommand,
							commandName
					),
					isSubcommand,
					null
			))).queue();
		}

		if (checkDuplicateArgument(argName, existingArguments, isSubcommand)) {
			return null;
		}

		return Map.entry(argName, argValue);
	}

	private @Nullable Map.Entry<String, String> getIndexedArgument(
			String arg,
			int index,
			Map<String, String> existingArguments,
			List<BotConfigCommandArgument<?>> argumentDefinitions,
			boolean isSubcommand
	) {
		String argName = argumentDefinitions.get(index).getName();

		if (checkDuplicateArgument(argName, existingArguments, isSubcommand)) {
			return null;
		}

		return Map.entry(argName, arg);
	}

	private boolean checkDuplicateArgument(
			String argName,
			Map<String, String> existingArguments,
			boolean isSubcommand
	) {
		if (existingArguments.containsKey(argName)) {
			event.getChannel().sendMessage(discordMessageService.createMessage(getErrorMessage(
					"errors.command_duplicate_argument",
					"errors.subcommand_duplicate_argument",
					Lists.newArrayList(),
					Lists.newArrayList(
							argName,
							commandName
					),
					Lists.newArrayList(),
					Lists.newArrayList(
							argName,
							subcommand,
							commandName
					),
					isSubcommand,
					null
			))).queue();
			return true;
		}

		return false;
	}

	private BotConfigMessage getErrorMessage(
			String commandKey,
			String subcommandKey,
			List<String> commandErrorTitlePlaceholders,
			List<String> commandErrorDescriptionPlaceholders,
			List<String> subcommandErrorTitlePlaceholders,
			List<String> subcommandErrorDescriptionPlaceholders,
			boolean isSubcommand,
			BotLanguages language
	) {
		if (isSubcommand) {
			String titleKey = subcommandKey + ".title";
			String descriptionKey = subcommandKey + ".description";
			return errorMessageService.getErrorMessage(
					titleKey,
					descriptionKey,
					subcommandErrorTitlePlaceholders,
					subcommandErrorDescriptionPlaceholders,
					Optional.ofNullable(language)
			);
		} else {
			String titleKey = commandKey + ".title";
			String descriptionKey = commandKey + ".description";
			return errorMessageService.getErrorMessage(
					titleKey,
					descriptionKey,
					commandErrorTitlePlaceholders,
					commandErrorDescriptionPlaceholders,
					Optional.ofNullable(language)
			);
		}
	}
}