package com.akjostudios.acsp.bot.util.command.argument;

import com.akjostudios.acsp.bot.config.bot.command.BotConfigCommand;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.List;

public class BotCommandArgumentParser {
	private static final Logger LOGGER = LoggerManager.getLogger(BotCommandArgumentParser.class);

	private final String commandName;
	private final BotConfigCommand commandDefinition;
	private final List<String> args;
	private final String subcommand;
	private final List<String> subcommandArgs;

	private final MessageReceivedEvent event;

	private BotCommandArgumentParser(String commandName, BotConfigCommand commandDefinition, List<String> args, MessageReceivedEvent event) {
		this.commandName = commandName;
		this.commandDefinition = commandDefinition;
		this.args = args;
		this.subcommand = null;
		this.subcommandArgs = null;
		this.event = event;
	}

	private BotCommandArgumentParser(String commandName, BotConfigCommand commandDefinition, List<String> args, String subcommand, List<String> subcommandArgs, MessageReceivedEvent event) {
		this.commandName = commandName;
		this.commandDefinition = commandDefinition;
		this.args = args;
		this.subcommand = subcommand;
		this.subcommandArgs = subcommandArgs;
		this.event = event;
	}
}