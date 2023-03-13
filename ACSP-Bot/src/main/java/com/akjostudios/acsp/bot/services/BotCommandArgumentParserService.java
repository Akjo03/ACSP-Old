package com.akjostudios.acsp.bot.services;

import com.akjostudios.acsp.bot.config.bot.command.BotConfigCommand;
import com.akjostudios.acsp.bot.config.bot.command.BotConfigSubcommand;
import com.akjostudios.acsp.bot.constants.BotLanguages;
import com.akjostudios.acsp.bot.util.command.argument.BotCommandArgumentParser;
import com.akjostudios.acsp.bot.util.command.permission.BotCommandPermissionParser;
import com.akjostudios.acsp.bot.util.command.permission.BotCommandPermissionValidator;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class BotCommandArgumentParserService {
	private static final Logger LOGGER = LoggerManager.getLogger(BotCommandArgumentParserService.class);

	private final BotStringsService botStringsService;
	private final DiscordMessageService discordMessageService;
	private final ErrorMessageService errorMessageService;
	private final CommandHelperService commandHelperService;

	public BotCommandArgumentParser getArgumentParser(
			String commandName,
			BotConfigCommand commandDefinition,
			String argStr,
			MessageReceivedEvent event
	) {
		// If we don't have subcommands, just parse the arguments
		if (!commandDefinition.getSubcommands().isAvailable()) {
			List<String> args = splitArguments(argStr);
			return new BotCommandArgumentParser(commandName, commandDefinition, args, event);
		}

		boolean hasSubcommand = argStr.trim().split(" ").length > 0;

		// If we have subcommands, but the user didn't provide one, check if it's required
		if (!hasSubcommand) {
			return checkIfSubcommandsRequired(commandName, commandDefinition, argStr, event);
		}

		// Get the subcommand name
		String subcommandName = argStr.trim().split(" ")[0];

		// If the subcommand name is empty or just the argument separator, check if it's required
		if (subcommandName.isEmpty()) {
			return checkIfSubcommandsRequired(commandName, commandDefinition, argStr, event);
		}
		if (subcommandName.trim().equals(";")) {
			return checkIfSubcommandsRequired(commandName, commandDefinition, argStr, event);
		}

		// Get the subcommand definition
		BotConfigSubcommand subcommandDefinition = commandDefinition.getSubcommands().getDefinitions().stream()
				.filter(subcommand -> subcommand.getName().equals(subcommandName))
				.findFirst()
				.orElse(null);

		// If the subcommand doesn't exist, send an error message
		if (subcommandDefinition == null) {
			LOGGER.info("User " + event.getAuthor().getAsTag() + " tried to execute command \"" + commandName + "\" but subcommand \"" + subcommandName + "\" does not exist!");

			String closestSubcommand = commandHelperService.closestSubcommand(commandName, subcommandName);

			event.getChannel().sendMessage(discordMessageService.createMessage(
					errorMessageService.getErrorMessage(
							"errors.subcommand_unknown.title",
							"errors.subcommand_unknown.description",
							List.of(),
							List.of(
									subcommandName,
									commandName,
									closestSubcommand != null ? botStringsService.getString("errors.special.similar_command", Optional.of(BotLanguages.ENGLISH), closestSubcommand) : ""
							),
							Optional.empty()
					)
			)).queue();

			return null;
		}

		// If the subcommand exists, but is currently not available, send an error message
		if (!subcommandDefinition.isAvailable()) {
			LOGGER.info("User " + event.getAuthor().getAsTag() + " tried to execute command \"" + commandName + "\" but subcommand \"" + subcommandName + "\" is not available!");

			event.getChannel().sendMessage(discordMessageService.createMessage(
					errorMessageService.getErrorMessage(
							"errors.subcommand_unavailable.title",
							"errors.subcommand_unavailable.description",
							List.of(),
							List.of(subcommandName, commandName),
							Optional.empty()
					)
			)).queue();

			return null;
		}

		// Check permissions for subcommand and send error message if user doesn't have permission
		BotCommandPermissionParser permissionParser = new BotCommandPermissionParser(subcommandName, subcommandDefinition.getPermissions());
		BotCommandPermissionValidator permissionValidator = permissionParser.parse();
		if (!permissionValidator.validate(event.getGuildChannel(), event.getMember())) {
			LOGGER.info("User " + event.getAuthor().getAsTag() + " tried to execute subcommand \"" + subcommandName + "\" of command \"" + commandName + "\" but doesn't have permission!");

			event.getChannel().sendMessage(discordMessageService.createMessage(
					errorMessageService.getErrorMessage(
							"errors.subcommand_missing_permissions.title",
							"errors.subcommand_missing_permissions.description",
							List.of(),
							List.of(subcommandName, commandName),
							Optional.empty()
					)
			)).queue();

			return null;
		}

		// Subcommand args are before ; and command args are after ; if no ; is present, command args are empty
		List<String> splitArgs = Arrays.asList(argStr.split(";"));
		String subcommandArgsStr = !splitArgs.isEmpty() ? splitArgs.get(0).replaceFirst(subcommandName, "").trim() : "";
		String commandArgsStr = splitArgs.size() > 1 ? splitArgs.get(1).trim() : "";

		// Split the subcommand args and command args
		List<String> subcommandArgs = splitArguments(subcommandArgsStr).stream()
				.filter(arg -> !arg.isEmpty())
				.toList();
		List<String> commandArgs = splitArguments(commandArgsStr).stream()
				.filter(arg -> !arg.isEmpty())
				.toList();

		return new BotCommandArgumentParser(commandName, commandDefinition, commandArgs, subcommandName, subcommandArgs, event);
	}

	private @NotNull List<String> splitArguments(String argString) {
		List<String> argumentsList = new ArrayList<>();
		Matcher matcher = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(argString);
		while (matcher.find()) {
			argumentsList.add(matcher.group(1).replace("\"", ""));
		}
		return argumentsList;
	}


	private @Nullable BotCommandArgumentParser checkIfSubcommandsRequired(String commandName, BotConfigCommand commandDefinition, String argStr, MessageReceivedEvent event) {
		if (commandDefinition.getSubcommands().isRequired()) {
			LOGGER.info("User " + event.getAuthor().getAsTag() + " tried to use command " + commandName + " without a subcommand!");

			event.getChannel().sendMessage(discordMessageService.createMessage(
					errorMessageService.getErrorMessage(
							"errors.subcommand_required.title",
							"errors.subcommand_required.description",
							List.of(),
							List.of(commandName),
							Optional.empty()
					)
			)).queue();

			return null;
		}

		List<String> args = splitArguments(argStr);
		return new BotCommandArgumentParser(commandName, commandDefinition, args, event);
	}
}