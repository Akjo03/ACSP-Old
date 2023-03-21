package com.akjostudios.acsp.bot.handlers;

import com.akjostudios.acsp.bot.constants.AcspDiscordChannels;
import com.akjostudios.acsp.bot.services.BotConfigService;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BeginChannelHandler extends ListenerAdapter {
	private static final Logger LOGGER = LoggerManager.getLogger(BeginChannelHandler.class);

	private final BotConfigService botConfigService;

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) {
			return;
		}

		if (event.getChannel().getIdLong() != AcspDiscordChannels.BEGIN_CHANNEL.getId()) {
			return;
		}

		String commandPrefix = botConfigService.getCommandPrefix();

		if (!event.getMessage().getContentRaw().equals(commandPrefix + "begin")) {
			event.getMessage().delete().queue();
			LOGGER.info("Deleted message from user " + event.getAuthor().getAsTag() + " in the begin channel, because it was not a begin command.");
		}
	}
}