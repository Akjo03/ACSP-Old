package com.akjostudios.acsp.bot.services;

import com.akjostudios.acsp.bot.config.bot.command.BotConfigCommand;
import com.akjostudios.acsp.bot.config.bot.command.BotConfigSubcommand;
import com.akjostudios.acsp.bot.config.bot.command.BotConfigSubcommands;
import com.akjostudios.acsp.bot.handlers.CommandsHandler;
import com.akjostudios.acsp.bot.util.command.BotCommand;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CommandHelperService {
	public String closestCommand(String commandName) {
		String closest = null;
		double highest = 0;
		for (String name : CommandsHandler.getAvailableCommands().stream().map(BotCommand::getName).toList()) {
			double current = new LevenshteinDistance(10).apply(commandName, name);
			if (current > highest) {
				highest = current;
				closest = name;
			}
		}
		if (highest < 5) {
			return null;
		}

		return closest;
	}

	public String closestSubcommand(String commandName, String subcommandName) {
		String closest = null;
		double highest = 0;
		for (String name : CommandsHandler.getAvailableCommands().stream()
				.filter(command -> command.getName().equals(commandName))
				.map(command -> command.getDefinition().getSubcommands())
				.filter(BotConfigSubcommands::isAvailable)
				.map(BotConfigSubcommands::getDefinitions)
				.flatMap(Collection::stream)
				.map(BotConfigSubcommand::getName)
				.toList()) {
			double current = new LevenshteinDistance(10).apply(subcommandName, name);
			if (current > highest) {
				highest = current;
				closest = name;
			}
		}
		if (highest < 5) {
			return null;
		}

		return closest;
	}

	public @Nullable BotConfigSubcommand getSubcommandDefinitionFromCommand(BotConfigCommand commandDefinition, String subcommandName) {
		return commandDefinition.getSubcommands().getDefinitions().stream()
				.filter(subcommand -> subcommand.getName().equals(subcommandName))
				.findFirst()
				.orElse(null);
	}
}