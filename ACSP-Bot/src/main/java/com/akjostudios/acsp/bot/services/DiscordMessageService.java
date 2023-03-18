package com.akjostudios.acsp.bot.services;

import com.akjostudios.acsp.bot.config.bot.message.BotConfigMessage;
import com.akjostudios.acsp.bot.config.bot.message.embed.BotConfigMessageEmbed;
import com.akjostudios.acsp.bot.config.bot.message.embed.BotConfigMessageEmbedField;
import com.akjostudios.acsp.bot.constants.BotConstants;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class DiscordMessageService {
	public MessageCreateData createMessage(BotConfigMessage message) {
		MessageCreateBuilder messageBuilder = new MessageCreateBuilder();

		if (message == null) { return messageBuilder.build(); }

		messageBuilder.setContent(message.getContent());
		messageBuilder.setEmbeds(message.getEmbeds().stream().map(this::toMessageEmbed).toList());

		return messageBuilder.build();
	}

	public MessageEditData editMessage(BotConfigMessage message) {
		return MessageEditBuilder.fromCreateData(createMessage(message)).build();
	}

	private MessageEmbed toMessageEmbed(BotConfigMessageEmbed embed) {
		EmbedBuilder embedBuilder = new EmbedBuilder()
				.setAuthor(embed.getAuthor().getName(), embed.getAuthor().getUrl(), embed.getAuthor().getIconUrl())
				.setTitle(embed.getTitle(), embed.getUrl())
				.setDescription(embed.getDescription())
				.setColor(Color.decode(embed.getColor()))
				.setImage(embed.getImageUrl())
				.setThumbnail(embed.getThumbnailUrl())
				.setFooter(embed.getFooter().getText(), embed.getFooter().getIconUrl())
				.setTimestamp(
						embed.getFooter().getTimestamp() != null
								? BotConstants.DATE_TIME_FORMATTER.parse(
										embed.getFooter().getTimestamp(),
										Instant::from
									)
								: null
				);

		for (BotConfigMessageEmbedField field : embed.getFields()) {
			embedBuilder.addField(field.getName(), field.getValue(), field.isInline());
		}

		return embedBuilder.build();
	}
}