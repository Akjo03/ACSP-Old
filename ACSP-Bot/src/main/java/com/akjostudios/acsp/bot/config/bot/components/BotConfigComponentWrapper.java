package com.akjostudios.acsp.bot.config.bot.components;

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
public class BotConfigComponentWrapper {
	@JsonSerialize
	@JsonDeserialize
	@EqualsAndHashCode.Include
	private String label;

	@JsonSerialize
	@JsonDeserialize
	private BotConfigComponent component;

	@JsonCreator
	public BotConfigComponentWrapper(
			@JsonProperty("label") String label,
			@JsonProperty("component") BotConfigComponent component
	) {
		this.label = label;
		this.component = component;
	}
}