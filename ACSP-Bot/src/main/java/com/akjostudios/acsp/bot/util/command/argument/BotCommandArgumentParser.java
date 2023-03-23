package com.akjostudios.acsp.bot.util.command.argument;

import com.akjostudios.acsp.bot.config.bot.command.BotConfigCommand;
import com.akjostudios.acsp.bot.config.bot.command.BotConfigSubcommand;
import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.message.BotConfigMessage;
import com.akjostudios.acsp.bot.constants.BotCommandArgumentTypes;
import com.akjostudios.acsp.bot.constants.BotLanguages;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import com.akjostudios.acsp.bot.services.bot.BotConfigService;
import com.akjostudios.acsp.bot.services.bot.BotStringsService;
import com.akjostudios.acsp.bot.services.command.BotCommandArgumentParsingReportService;
import com.akjostudios.acsp.bot.services.command.CommandHelperService;
import com.akjostudios.acsp.bot.util.command.argument.transformation.BotCommandIntegerArgumentTransformer;
import com.akjostudios.acsp.bot.util.command.argument.transformation.BotCommandStringArgumentTransformer;
import com.akjostudios.acsp.bot.util.exception.AcspBotCommandArgumentParseException;
import com.google.common.collect.Lists;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BotCommandArgumentParser {
	private static final Logger LOGGER = LoggerManager.getLogger(BotCommandArgumentParser.class);

	private final String commandName;
	private final BotConfigCommand commandDefinition;
	private final List<String> argsStr;
	private final String subcommand;
	private final List<String> subcommandArgsStr;

	private final MessageReceivedEvent event;

	private DiscordMessageService discordMessageService;
	private ErrorMessageService errorMessageService;
	private CommandHelperService commandHelperService;
	private BotConfigService botConfigService;
	private BotCommandArgumentParsingReportService botCommandArgumentParsingReportService;
	private BotStringsService botStringsService;

	public BotCommandArgumentParser(String commandName, BotConfigCommand commandDefinition, List<String> argsStr, MessageReceivedEvent event) {
		this.commandName = commandName;
		this.commandDefinition = commandDefinition;
		this.argsStr = argsStr;
		this.subcommand = null;
		this.subcommandArgsStr = null;
		this.event = event;
	}

	public BotCommandArgumentParser(String commandName, BotConfigCommand commandDefinition, List<String> argsStr, String subcommand, List<String> subcommandArgsStr, MessageReceivedEvent event) {
		this.commandName = commandName;
		this.commandDefinition = commandDefinition;
		this.argsStr = argsStr;
		this.subcommand = subcommand;
		this.subcommandArgsStr = subcommandArgsStr;
		this.event = event;
	}

	public void setupServices(
			DiscordMessageService discordMessageService,
			ErrorMessageService errorMessageService,
			CommandHelperService commandHelperService,
			BotConfigService botConfigService,
			BotCommandArgumentParsingReportService botCommandArgumentParsingReportService,
			BotStringsService botStringsService
	) {
		this.discordMessageService = discordMessageService;
		this.errorMessageService = errorMessageService;
		this.commandHelperService = commandHelperService;
		this.botConfigService = botConfigService;
		this.botCommandArgumentParsingReportService = botCommandArgumentParsingReportService;
		this.botStringsService = botStringsService;
	}

	public BotCommandArguments parse() {
		Map<String, String> suppliedArgs = getSuppliedArguments(argsStr, commandDefinition.getArguments(), false);
		if (suppliedArgs == null) {
			return null;
		}
		if (checkRequiredArguments(suppliedArgs, commandDefinition.getArguments(), false)) {
			return null;
		}
		List<BotCommandArgument<?>> commandArgs = parseArguments(suppliedArgs, commandDefinition.getArguments(), false);
		if (commandArgs == null) {
			return null;
		}

		List<BotCommandArgument<?>> subcommandArgs = new ArrayList<>();
		if (subcommand != null) {
			BotConfigSubcommand subcommandDefinition = commandHelperService.getSubcommandDefinitionFromCommand(commandDefinition, subcommand);
			if (subcommandDefinition == null) {
				return null;
			}
			Map<String, String> suppliedSubcommandArgs = getSuppliedArguments(subcommandArgsStr, subcommandDefinition.getArguments(), true);
			if (suppliedSubcommandArgs == null) {
				return null;
			}
			if (checkRequiredArguments(suppliedSubcommandArgs, subcommandDefinition.getArguments(), true)) {
				return null;
			}
			subcommandArgs = parseArguments(suppliedSubcommandArgs, subcommandDefinition.getArguments(), true);
			if (subcommandArgs == null) {
				return null;
			}
		}

		return subcommand != null
				? BotCommandArguments.of(commandName, commandArgs, subcommand, subcommandArgs)
				: BotCommandArguments.of(commandName, commandArgs);
	}

	private @Nullable Map<String, String> getSuppliedArguments(
			List<String> argsList,
			List<BotConfigCommandArgument<?>> argumentDefinitions,
			boolean isSubcommand
	) {
		Map<String, String> suppliedArguments = new HashMap<>();
		// Used to check if key-value pairs are given exclusively or at the end of the argument list
		Map<Integer, Boolean> indexTypeMap = new HashMap<>();

		if (argsList == null || argsList.isEmpty()) {
			return suppliedArguments;
		}

		for (int i = 0; i < argsList.size(); i++) {
			String arg = argsList.get(i);

			if (indexTypeMap.containsValue(true) && !arg.contains("=")) {
				event.getChannel().sendMessage(discordMessageService.createMessage(
						errorMessageService.getErrorMessage(
								"errors.command_arguments_invalid_order.title",
								"errors.command_arguments_invalid_order.description",
								List.of(),
								List.of(),
								Optional.empty()
						)
				)).queue();
				return null;
			}

			if (argsList.size() > argumentDefinitions.size() && !indexTypeMap.containsValue(true)) {
				event.getChannel().sendMessage(discordMessageService.createMessage(getErrorMessage(
						"errors.command_arguments_too_many" + (argumentDefinitions.size() == 0 ? "_none" : ""),
						"errors.subcommand_arguments_too_many" + (argumentDefinitions.size() == 0 ? "_none" : ""),
						Lists.newArrayList(),
						Lists.newArrayList(
								commandName,
								String.valueOf(argumentDefinitions.size())
						),
						Lists.newArrayList(),
						Lists.newArrayList(
								subcommand,
								commandName,
								String.valueOf(argumentDefinitions.size())
						),
						isSubcommand,
						null
				))).queue();
				return null;
			}

			if (arg.contains("=")) {
				Map.Entry<String, String> namedArg = getNamedArgument(arg, suppliedArguments, argumentDefinitions, isSubcommand);
				if (namedArg == null) {
					return null;
				}
				suppliedArguments.put(namedArg.getKey(), namedArg.getValue());
				indexTypeMap.put(i, false);
			} else {
				Map.Entry<String, String> indexedArg = getIndexedArgument(arg, i, suppliedArguments, argumentDefinitions, isSubcommand);
				if (indexedArg == null) {
					return null;
				}
				suppliedArguments.put(indexedArg.getKey(), indexedArg.getValue());
				indexTypeMap.put(i, true);
			}
		}

		return suppliedArguments;
	}

	private boolean checkRequiredArguments(
			Map<String, String> suppliedArguments,
			List<BotConfigCommandArgument<?>> argumentDefinitions,
			boolean isSubcommand
	) {
		if (isSubcommand && subcommand == null) {
			return false;
		}

		for (BotConfigCommandArgument<?> argumentDefinition : argumentDefinitions) {
			if (argumentDefinition.isRequired() && !suppliedArguments.containsKey(argumentDefinition.getName())) {
				event.getChannel().sendMessage(discordMessageService.createMessage(getErrorMessage(
						"errors.command_required_argument_missing",
						"errors.subcommand_required_argument_missing",
						Lists.newArrayList(),
						Lists.newArrayList(
								commandName,
								argumentDefinition.getName()
						),
						Lists.newArrayList(),
						Lists.newArrayList(
								subcommand,
								commandName,
								argumentDefinition.getName()
						),
						isSubcommand,
						null
				))).queue();
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private List<BotCommandArgument<?>> parseArguments(
			Map<String, String> suppliedArguments,
			List<BotConfigCommandArgument<?>> argumentDefinitions,
			boolean isSubcommand
	) {
		if (isSubcommand && subcommand == null) {
			return List.of();
		}

		List<BotCommandArgument<?>> parsedArguments = new ArrayList<>();
		List<AcspBotCommandArgumentParseException> parseExceptions = new ArrayList<>();
		AtomicBoolean parsingFailed = new AtomicBoolean(false);

		for (BotConfigCommandArgument<?> argumentDefinition : argumentDefinitions) {
			BotCommandArgumentTypes argumentType = BotCommandArgumentTypes.fromString(argumentDefinition.getType());
			if (argumentType == null) {
				LOGGER.error("Failed to parse argument type for command " + commandName + " and subcommand " + subcommand + " with argument " + argumentDefinition.getName() + ": " + argumentDefinition.getType());
				return null;
			}

			String argumentValue = suppliedArguments.get(argumentDefinition.getName());

			BotCommandArgument<?> parsedArgument = switch (argumentType) {
				case INTEGER -> BotCommandIntegerArgumentTransformer.of(
						commandName,
						(BotConfigCommandArgument<Integer>) argumentDefinition,
						argumentValue,
						discordMessageService, botConfigService, errorMessageService, botStringsService
				).transform(event).ifError(e -> {
					handleParseException(e, parseExceptions, argumentDefinition);
					parsingFailed.set(true);
				}).getOrNull();
				case STRING -> BotCommandStringArgumentTransformer.of(
						commandName,
						(BotConfigCommandArgument<String>) argumentDefinition,
						argumentValue,
						discordMessageService, botConfigService, errorMessageService, botStringsService
				).transform(event).ifError(e -> {
					handleParseException(e, parseExceptions, argumentDefinition);
					parsingFailed.set(true);
				}).getOrNull();
			};
			if (parsedArgument == null) {
				continue;
			}

			parsedArguments.add(parsedArgument);
		}

		if (parsingFailed.get()) {
			if (!parseExceptions.isEmpty()) {
				event.getChannel().sendMessage(discordMessageService.createMessage(
						botCommandArgumentParsingReportService.getReportMessage(commandName, parseExceptions)
				)).queue();
			}

			return null;
		}

		return parsedArguments;
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

	private void handleParseException(Exception e, List<AcspBotCommandArgumentParseException> parseExceptions, BotConfigCommandArgument<?> argumentDefinition) {
		if (e instanceof AcspBotCommandArgumentParseException parseException) {
			parseExceptions.add(parseException);
		} else {
			LOGGER.error("Failed to parse argument " + argumentDefinition.getName() + " for command " + commandName + " and subcommand " + subcommand + ": " + e.getMessage());
		}
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