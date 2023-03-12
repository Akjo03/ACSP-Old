package com.akjostudios.acsp.bot.config.bot.message;

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
public class BotConfigMessageWrapper {
	@JsonSerialize
	@JsonDeserialize
	private String label;

	@JsonSerialize
	@JsonDeserialize
	private String language;

	@JsonSerialize
	@JsonDeserialize
	private BotConfigMessage message;

	@JsonCreator
	public BotConfigMessageWrapper(
			@JsonProperty("label") String label,
			@JsonProperty("language") String language,
			@JsonProperty("message") BotConfigMessage message
	) {
		this.label = label;
		this.language = language;
		this.message = message;
	}
}