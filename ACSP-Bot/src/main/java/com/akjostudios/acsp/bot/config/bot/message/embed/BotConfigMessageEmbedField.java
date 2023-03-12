package com.akjostudios.acsp.bot.config.bot.message.embed;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
@SuppressWarnings("unused")
public class BotConfigMessageEmbedField {
	@JsonSerialize
	@JsonDeserialize
	private String name;

	@JsonSerialize
	@JsonDeserialize
	private String value;

	@JsonSerialize
	@JsonDeserialize
	private boolean inline;

	@JsonCreator
	public BotConfigMessageEmbedField(
			@JsonProperty("name") String name,
			@JsonProperty("value") String value,
			@JsonProperty("inline") boolean inline
	) {
		this.name = name;
		this.value = value;
		this.inline = inline;
	}
}