package com.akjostudios.acsp.bot.config.bot.command.argument.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("unused")
public class BotConfigCommandArgumentStringData extends BotConfigCommandArgumentData<String> {
	@JsonSerialize
	@JsonDeserialize
	private Integer minLength;

	@JsonSerialize
	@JsonDeserialize
	private Integer maxLength;

	@JsonSerialize
	@JsonDeserialize
	private String regex;

	@JsonSerialize
	@JsonDeserialize
	private String defaultValue;

	@JsonCreator
	public BotConfigCommandArgumentStringData(
			@JsonProperty("min_length") Integer minLength,
			@JsonProperty("max_length") Integer maxLength,
			@JsonProperty("regex") String regex,
			@JsonProperty("default") String defaultValue
	) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.regex = regex;
		this.defaultValue = defaultValue;
	}
}