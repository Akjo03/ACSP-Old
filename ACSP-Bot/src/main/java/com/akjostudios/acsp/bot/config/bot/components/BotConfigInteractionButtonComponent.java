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
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("unused")
public class BotConfigInteractionButtonComponent extends BotConfigComponent {
	@JsonSerialize
	@JsonDeserialize
	private String interactionId;

	@JsonSerialize
	@JsonDeserialize
	private String style;

	@JsonSerialize
	@JsonDeserialize
	private String text;

	@JsonSerialize
	@JsonDeserialize
	private BotConfigInteractionButtonComponentEmoji emoji;

	@JsonSerialize
	@JsonDeserialize
	private boolean disabled;

	@JsonCreator
	public BotConfigInteractionButtonComponent(
			@JsonProperty("interaction_id") String interactionId,
			@JsonProperty("style") String style,
			@JsonProperty("text") String text,
			@JsonProperty("emoji") BotConfigInteractionButtonComponentEmoji emoji,
			@JsonProperty("disabled") boolean disabled
	) {
		this.interactionId = interactionId;
		this.style = style;
		this.text = text;
		this.emoji = emoji;
		this.disabled = disabled;
	}
}