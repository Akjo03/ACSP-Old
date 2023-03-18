package com.akjostudios.acsp.bot.commands;

import com.akjostudios.acsp.bot.util.command.BotCommand;
import com.akjostudios.acsp.bot.util.command.BotCommandInitializer;
import com.akjostudios.acsp.bot.util.command.argument.BotCommandArguments;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class RestartCommand extends BotCommand {
	private static final Logger LOGGER = LoggerManager.getLogger(RestartCommand.class);

	protected RestartCommand() {
		super("restart");
	}

	@Override
	public void initialize(@NotNull BotCommandInitializer initializer) {}

	@Override
	public void execute(@NotNull MessageReceivedEvent event, BotCommandArguments arguments) {
		LOGGER.info("Restart");
	}
}