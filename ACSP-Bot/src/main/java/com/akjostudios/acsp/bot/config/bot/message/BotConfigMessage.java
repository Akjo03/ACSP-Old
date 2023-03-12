package com.akjostudios.acsp.bot.config.bot.message;

import com.akjostudios.acsp.bot.config.bot.message.embed.BotConfigMessageEmbed;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuppressWarnings("unused")
public class BotConfigMessage {
	@JsonSerialize
	@JsonDeserialize
	private String content;

	@JsonSerialize
	@JsonDeserialize
	private List<BotConfigMessageEmbed> embeds;

	@JsonCreator
	public BotConfigMessage(
			@JsonProperty("content") String content,
			@JsonProperty("embeds") List<BotConfigMessageEmbed> embeds
	) {
		this.content = content;
		this.embeds = embeds;
	}
}