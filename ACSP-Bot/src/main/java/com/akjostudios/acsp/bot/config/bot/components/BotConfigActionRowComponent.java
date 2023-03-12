package com.akjostudios.acsp.bot.config.bot.components;

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
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("unused")
public class BotConfigActionRowComponent extends BotConfigComponent {
	@JsonSerialize
	@JsonDeserialize
	private List<BotConfigComponent> components;

	@JsonCreator
	public BotConfigActionRowComponent(
			@JsonProperty("components") List<BotConfigComponent> components
	) {
		this.components = components;
	}
}