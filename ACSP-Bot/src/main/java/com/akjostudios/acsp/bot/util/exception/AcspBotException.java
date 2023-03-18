package com.akjostudios.acsp.bot.util.exception;

import com.akjostudios.acsp.bot.config.bot.message.BotConfigMessage;
import com.akjostudios.acsp.bot.constants.AcspDiscordChannels;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import io.github.akjo03.lib.logging.Logger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.Optional;

public abstract class AcspBotException extends RuntimeException {
	@Serial private static final long serialVersionUID = 1L;

	private final BotConfigMessage message;

	private final DiscordMessageService discordMessageService;
	private final ErrorMessageService errorMessageService;

	private final Logger logger;

	public AcspBotException(String message, Throwable cause, DiscordMessageService discordMessageService, ErrorMessageService errorMessageService, Logger logger) {
		this(null, message, cause, discordMessageService, errorMessageService, logger);
	}

	public AcspBotException(BotConfigMessage message, DiscordMessageService discordMessageService) {
		this(message, null, null, discordMessageService, null, null);
	}

	public AcspBotException(BotConfigMessage message, String internalMessage, Throwable cause, DiscordMessageService discordMessageService, ErrorMessageService errorMessageService, Logger logger) {
		super(internalMessage, cause);
		this.message = message;
		this.discordMessageService = discordMessageService;
		this.errorMessageService = errorMessageService;
		this.logger = logger;
	}

	public void sendMessage(@NotNull GuildMessageChannel channel) {
		if (message != null) {
			channel.sendMessage(discordMessageService.createMessage(message)).queue();
		} else if (getMessage() != null) {
			if (logger != null) { logger.error(getMessage(), getCause()); }

			channel.sendMessage(discordMessageService.createMessage(
					errorMessageService.getInternalErrorMessage(
							getMessage(), Optional.empty()
					)
			)).queue();
		}
	}

	public void sendMessage(@NotNull JDA jdaInstance, @NotNull AcspDiscordChannels channel) {
		GuildMessageChannel guildChannel = jdaInstance.getTextChannelById(channel.getId());
		if (guildChannel != null) {
			sendMessage(guildChannel);
		}
	}
}