package com.akjostudios.acsp.bot.commands;

import com.akjostudios.acsp.bot.AcspBot;
import com.akjostudios.acsp.bot.constants.BotCommandArgumentTypes;
import com.akjostudios.acsp.bot.util.command.BotCommand;
import com.akjostudios.acsp.bot.util.command.BotCommandInitializer;
import com.akjostudios.acsp.bot.util.command.argument.BotCommandArguments;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
		LOGGER.info("Executing restart command... See you soon!");

		Integer delay = arguments.getCommandArgument("delay", BotCommandArgumentTypes.INTEGER);
		if (delay == null) {
			delay = 0;
		}

		ScheduledThreadPoolExecutor restartExec = new ScheduledThreadPoolExecutor(1);
		restartExec.schedule(() -> {
			event.getChannel().sendMessage(getDiscordMessageService().createMessage(
					getBotConfigService().getMessageDefinition(
							"RESTART_MESSAGE",
							Optional.empty()
					)
			)).submit().thenRun(AcspBot::restart);
		}, delay, TimeUnit.SECONDS);
	}
}