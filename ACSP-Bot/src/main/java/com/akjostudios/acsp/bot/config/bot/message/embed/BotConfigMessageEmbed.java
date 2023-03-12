package com.akjostudios.acsp.bot.config.bot.message.embed;

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
public class BotConfigMessageEmbed {
	@JsonSerialize
	@JsonDeserialize
	private BotConfigMessageEmbedAuthor author;

	@JsonSerialize
	@JsonDeserialize
	private String title;

	@JsonSerialize
	@JsonDeserialize
	private String description;

	@JsonSerialize
	@JsonDeserialize
	private String url;

	@JsonSerialize
	@JsonDeserialize
	private String color;

	@JsonSerialize
	@JsonDeserialize
	private List<BotConfigMessageEmbedField> fields;

	@JsonSerialize
	@JsonDeserialize
	private String imageUrl;

	@JsonSerialize
	@JsonDeserialize
	private String thumbnailUrl;

	@JsonSerialize
	@JsonDeserialize
	private BotConfigMessageEmbedFooter footer;

	@JsonCreator
	public BotConfigMessageEmbed(
			@JsonProperty("author") BotConfigMessageEmbedAuthor author,
			@JsonProperty("title") String title,
			@JsonProperty("description") String description,
			@JsonProperty("url") String url,
			@JsonProperty("color") String color,
			@JsonProperty("fields") List<BotConfigMessageEmbedField> fields,
			@JsonProperty("image_url") String imageUrl,
			@JsonProperty("thumbnail_url") String thumbnailUrl,
			@JsonProperty("footer") BotConfigMessageEmbedFooter footer
	) {
		this.author = author;
		this.title = title;
		this.description = description;
		this.url = url;
		this.color = color;
		this.fields = fields;
		this.imageUrl = imageUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.footer = footer;
	}
}