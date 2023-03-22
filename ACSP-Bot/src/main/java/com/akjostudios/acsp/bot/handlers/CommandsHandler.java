package com.akjostudios.acsp.bot.handlers;

import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import com.akjostudios.acsp.bot.services.bot.BotConfigService;
import com.akjostudios.acsp.bot.services.bot.BotStringsService;
import com.akjostudios.acsp.bot.services.command.BotCommandArgumentParserService;
import com.akjostudios.acsp.bot.services.command.CommandHelperService;
import com.akjostudios.acsp.bot.util.command.BotCommand;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommandsHandler extends ListenerAdapter {
	private static final Logger LOGGER = LoggerManager.getLogger(CommandsHandler.class);

	private static final List<BotCommand> availableCommands = new ArrayList<>();

	private final BotConfigService botConfigService;
	private final BotStringsService botStringsService;
	private final ErrorMessageService errorMessageService;
	private final CommandHelperService commandHelperService;
	private final DiscordMessageService discordMessageService;
	private final BotCommandArgumentParserService botCommandArgumentParserService;

	public static void setAvailableCommands(List<BotCommand> availableCommands) {
		CommandsHandler.availableCommands.addAll(availableCommands);
	}

	public static List<BotCommand> getAvailableCommands() {
		return availableCommands;
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		// If the message was sent by a bot and/or was not sent in a guild, ignore it
		if (event.getAuthor().isBot()) { return; }
		if (!event.isFromGuild()) { return; }

		// If the message does not start with the command prefix, ignore it
		String commandPrefix = botConfigService.getCommandPrefix();
		if (!event.getMessage().getContentRaw().startsWith(commandPrefix)) { return; }

		// Parse the command and its parts
		String command = event.getMessage().getContentRaw().substring(commandPrefix.length());
		List<String> commandParts = Arrays.stream(command.split(" ")).toList();

		// If the command is empty, ignore it
		if (commandParts.isEmpty()) { return; }

		// Split the command name and its arguments
		String commandName = commandParts.get(0);
		String commandArgStr = commandParts.stream().skip(1).reduce((a, b) -> a + " " + b).orElse("");

		// Get the corresponding command
		BotCommand botCommand = availableCommands.stream()
				.filter(commandP -> commandP.getName().equals(commandName))
				.findFirst().orElse(null);
		// If the command was not found, send an error message to the user
		if (botCommand == null) {
			LOGGER.info("User " + event.getAuthor().getAsTag() + " tried to execute command \"" + commandName + "\" but it was not found!");
			String closestCommand = commandHelperService.closestCommand(commandName);
			event.getChannel().sendMessage(discordMessageService.createMessage(
					errorMessageService.getErrorMessage(
							"errors.unknown_command.title",
							"errors.unknown_command.description",
							List.of(),
							List.of(
									commandName,
									closestCommand != null
											? botStringsService.getString("errors.special.similar_command", Optional.empty(), closestCommand)
											: ""
							),
							Optional.empty()
					)
			)).queue();
			return;
		}

		// Execute the command
		botCommand.executeInternal(event, commandArgStr);
	}
}
