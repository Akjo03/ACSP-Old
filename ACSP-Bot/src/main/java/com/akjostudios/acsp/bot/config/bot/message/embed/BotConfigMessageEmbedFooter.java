package com.akjostudios.acsp.bot.config.bot.message.embed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuppressWarnings("unused")
public class BotConfigMessageEmbedFooter {
	@JsonSerialize
	@JsonDeserialize
	private String text;

	@JsonSerialize
	@JsonDeserialize
	private String timestamp;

	@JsonSerialize
	@JsonDeserialize
	private String iconUrl;

	@JsonCreator
	public BotConfigMessageEmbedFooter(
			@JsonProperty("text") String text,
			@JsonProperty("timestamp") String timestamp,
			@JsonProperty("icon_url") String iconUrl
	) {
		this.text = text;
		this.timestamp = timestamp;
		this.iconUrl = iconUrl;
	}
}