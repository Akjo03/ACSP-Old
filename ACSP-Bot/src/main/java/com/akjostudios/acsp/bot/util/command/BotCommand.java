package com.akjostudios.acsp.bot.util.command;

import com.akjostudios.acsp.bot.config.bot.command.BotConfigCommand;
import com.akjostudios.acsp.bot.services.BotConfigService;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Optional;

public abstract class BotCommand {
	private static Logger LOGGER;

	protected final String name;
	private BotConfigCommand definition;

	private BotConfigService botConfigService;
	private DiscordMessageService discordMessageService;
	private ErrorMessageService errorMessageService;

	protected BotCommand(String name) {
		LOGGER = LoggerManager.getLogger(getClass());

		this.name = name;
	}

	public void setupServices(
			BotConfigService botConfigService,
			DiscordMessageService discordMessageService,
			ErrorMessageService errorMessageService
	) {
		this.botConfigService = botConfigService;
		this.discordMessageService = discordMessageService;
		this.errorMessageService = errorMessageService;
	}

	public abstract void initialize(@NotNull BotCommandInitializer initializer);
	public abstract void execute(@NotNull MessageReceivedEvent event);

	public void initializeInternal(@NotNull ApplicationContext applicationContext, @NotNull JDA jdaInstance) {
		// Get the definition of the command from the bot config
		BotConfigCommand definition = botConfigService.getCommandDefinition(name, Optional.empty());
		if (definition == null) {
			LOGGER.error("Command definition for " + name + " not found!");
		}
		this.definition = definition;

		// Call the initialize method of the command
		initialize(BotCommandInitializer.of(applicationContext, jdaInstance));
	}

	public void executeInternal(MessageReceivedEvent event, String commandArgsStr) {
		if (definition == null) {
			return;
		}

		// Check if command is available, if not, send error message
		if (!definition.isAvailable()) {
			LOGGER.info("User " + event.getAuthor().getAsTag() + " tried to use unavailable command \"" + name + "\"!");

			event.getChannel().sendMessage(discordMessageService.createMessage(
					errorMessageService.getErrorMessage(
							"errors.command_unavailable.title",
							"errors.command_unavailable.description",
							List.of(),
							List.of(
									name
							),
							Optional.empty()
					)
			)).queue();
		}

		execute(event);
	}

	// -------------- Getters --------------

	public String getName() {
		return name;
	}

	public BotConfigCommand getDefinition() {
		return definition;
	}

	protected BotConfigService getBotConfigService() {
		return botConfigService;
	}

	protected DiscordMessageService getDiscordMessageService() {
		return discordMessageService;
	}

	protected ErrorMessageService getErrorMessageService() {
		return errorMessageService;
	}
}