package com.akjostudios.acsp.bot.config.bot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import net.dv8tion.jda.api.entities.emoji.Emoji;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuppressWarnings("unused")
public class BotConfigInteractionButtonComponentEmoji {
	@JsonSerialize
	@JsonDeserialize
	private String name;

	@JsonSerialize
	@JsonDeserialize
	private boolean animated;

	@JsonCreator
	public BotConfigInteractionButtonComponentEmoji(
			@JsonProperty("name") String name,
			@JsonProperty("animated") boolean animated
	) {
		this.name = name;
		this.animated = animated;
	}

	public Emoji toEmoji() {
		return Emoji.fromUnicode(name);
	}
}