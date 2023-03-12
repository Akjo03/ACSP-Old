package com.akjostudios.acsp.bot.config.bot.field;

import com.akjostudios.acsp.bot.config.bot.message.embed.BotConfigMessageEmbedField;
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
public class BotConfigFieldWrapper {
	@JsonSerialize
	@JsonDeserialize
	private String label;

	@JsonSerialize
	@JsonDeserialize
	private String language;

	@JsonSerialize
	@JsonDeserialize
	private BotConfigMessageEmbedField field;

	@JsonCreator
	public BotConfigFieldWrapper(
			@JsonProperty("label") String label,
			@JsonProperty("language") String language,
			@JsonProperty("message") BotConfigMessageEmbedField field
	) {
		this.label = label;
		this.language = language;
		this.field = field;
	}
}