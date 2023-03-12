package com.akjostudios.acsp.bot.config.bot.command;

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
public class BotConfigSubcommands {
	@JsonSerialize
	@JsonDeserialize
	private boolean available;

	@JsonSerialize
	@JsonDeserialize
	private List<BotConfigSubcommand> definitions;

	@JsonSerialize
	@JsonDeserialize
	private boolean required;

	@JsonCreator
	public BotConfigSubcommands(
			@JsonProperty("available") boolean available,
			@JsonProperty("definitions") List<BotConfigSubcommand> definitions,
			@JsonProperty("required") boolean required
	) {
		this.available = available;
		this.definitions = definitions;
		this.required = required;
	}
}