package com.akjostudios.acsp.bot.config.bot.command.argument.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("unused")
public class BotConfigCommandArgumentIntegerData extends BotConfigCommandArgumentData<Integer> {
	@JsonSerialize
	@JsonDeserialize
	private Integer minValue;

	@JsonSerialize
	@JsonDeserialize
	private Integer maxValue;

	@JsonSerialize
	@JsonDeserialize
	private Integer defaultValue;

	public BotConfigCommandArgumentIntegerData(
			@JsonProperty("min") Integer minValue,
			@JsonProperty("max") Integer maxValue,
			@JsonProperty("default") Integer defaultValue
	) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.defaultValue = defaultValue;
	}
}