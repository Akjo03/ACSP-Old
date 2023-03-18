package com.akjostudios.acsp.bot.util.exception;

import com.akjostudios.acsp.bot.config.bot.message.BotConfigMessage;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;

public abstract class AcspBotException extends RuntimeException {
	@Serial private static final long serialVersionUID = 1L;

	private final BotConfigMessage message;

	private final DiscordMessageService discordMessageService;

	public AcspBotException(BotConfigMessage message, DiscordMessageService discordMessageService) {
		this.message = message;
		this.discordMessageService = discordMessageService;
	}

	public void sendMessage(@NotNull GuildMessageChannelUnion channel) {
		channel.sendMessage(discordMessageService.createMessage(message)).queue();
	}
}