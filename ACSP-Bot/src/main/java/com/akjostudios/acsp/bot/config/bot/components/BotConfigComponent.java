package com.akjostudios.acsp.bot.config.bot.components;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
		@JsonSubTypes.Type(value = BotConfigActionRowComponent.class, name = "ACTION_ROW"),
		@JsonSubTypes.Type(value = BotConfigInteractionButtonComponent.class, name = "INTERACTION_BUTTON")
})
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuppressWarnings("unused")
public abstract class BotConfigComponent {
	@JsonSerialize
	@JsonDeserialize
	private String type;

	@JsonCreator
	protected BotConfigComponent(
			@JsonProperty("type") String type
	) {
		this.type = type;
	}
}