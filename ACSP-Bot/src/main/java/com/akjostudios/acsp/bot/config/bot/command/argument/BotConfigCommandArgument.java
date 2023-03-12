package com.akjostudios.acsp.bot.config.bot.command.argument;

import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuppressWarnings("unused")
public class BotConfigCommandArgument<T> {
	@JsonSerialize
	@JsonDeserialize
	@EqualsAndHashCode.Include
	private String name;

	@JsonSerialize
	@JsonDeserialize
	private String type;

	@JsonSerialize
	@JsonDeserialize
	private String description;

	@JsonSerialize
	@JsonDeserialize
	private boolean required;

	@JsonSerialize
	@JsonDeserialize
	private BotConfigCommandArgumentData<T> data;

	@JsonCreator
	public BotConfigCommandArgument(
			@JsonProperty("name") String name,
			@JsonProperty("type") String type,
			@JsonProperty("description") String description,
			@JsonProperty("required") boolean required,
			@JsonProperty("data") BotConfigCommandArgumentData<T> data
	) {
		this.name = name;
		this.type = type;
		this.description = description;
		this.required = required;
		this.data = data;
	}
}