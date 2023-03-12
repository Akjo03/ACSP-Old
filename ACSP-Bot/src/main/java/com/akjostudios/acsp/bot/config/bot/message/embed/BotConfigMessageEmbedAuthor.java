package com.akjostudios.acsp.bot.config.bot.message.embed;

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
public class BotConfigMessageEmbedAuthor {
	@JsonSerialize
	@JsonDeserialize
	private String name;

	@JsonSerialize
	@JsonDeserialize
	private String url;

	@JsonSerialize
	@JsonDeserialize
	private String iconURL;

	@JsonCreator
	public BotConfigMessageEmbedAuthor(
			@JsonProperty("name") String name,
			@JsonProperty("url") String url,
			@JsonProperty("icon_url") String iconURL
	) {
		this.name = name;
		this.url = url;
		this.iconURL = iconURL;
	}
}