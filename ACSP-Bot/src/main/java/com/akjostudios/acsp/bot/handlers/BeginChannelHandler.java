package com.akjostudios.acsp.bot.handlers;

import com.akjostudios.acsp.bot.constants.AcspDiscordChannels;
import com.akjostudios.acsp.bot.services.bot.BotConfigService;
import com.akjostudios.acsp.bot.util.handlers.AcspBotHandler;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BeginChannelHandler extends ListenerAdapter implements AcspBotHandler<BeginChannelHandler> {
	private static final Logger LOGGER = LoggerManager.getLogger(BeginChannelHandler.class);

	private final BotConfigService botConfigService;

	@Override
	@SuppressWarnings("CodeBlock2Expr")
	public BeginChannelHandler setup(JDA jdaInstance) {
		TextChannel beginChannel = jdaInstance.getTextChannelById(AcspDiscordChannels.BEGIN_CHANNEL.getId());
		if (beginChannel == null) { return this; }

		MessagePaginationAction history = beginChannel.getIterableHistory();
		int messageBatchSize = 100;

		history.queue(messages -> {
			while (!messages.isEmpty()) {
				List<String> messageIds = messages.stream()
						.map(Message::getId)
						.toList();

				for (int i = 0; i < messageIds.size(); i += messageBatchSize) {
					int endIndex = Math.min(i + messageBatchSize, messageIds.size());
					List<String> batchIds = messageIds.subList(i, endIndex);

					if (batchIds.isEmpty()) {
						continue;
					}
					if (batchIds.size() == 1) {
						beginChannel.deleteMessageById(batchIds.get(0)).queue(null, error -> {
							LOGGER.error("Failed to delete message in the begin channel.", error);
						});
					} else {
						beginChannel.deleteMessagesByIds(batchIds).queue(null, error -> {
							LOGGER.error("Failed to delete messages in the begin channel.", error);
						});
					}
				}

				messages = history.submit().join();
			}
		});

		return this;
	}

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