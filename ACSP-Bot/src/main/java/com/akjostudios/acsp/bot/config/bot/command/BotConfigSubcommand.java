package com.akjostudios.acsp.bot.config.bot.command;

import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.command.permission.BotConfigCommandPermission;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuppressWarnings("unused")
public class BotConfigSubcommand {
	@JsonSerialize
	@JsonDeserialize
	private String name;

	@JsonSerialize
	@JsonDeserialize
	private boolean available;

	@JsonSerialize
	@JsonDeserialize
	private String description;

	@JsonSerialize
	@JsonDeserialize
	private List<BotConfigCommandArgument<?>> arguments;

	@JsonSerialize
	@JsonDeserialize
	private List<BotConfigCommandPermission> permissions;

	@JsonCreator
	public BotConfigSubcommand(
			@JsonProperty("name") String name,
			@JsonProperty("available") boolean available,
			@JsonProperty("description") String description,
			@JsonProperty("arguments") List<BotConfigCommandArgument<?>> arguments,
			@JsonProperty("permissions") List<BotConfigCommandPermission> permissions
	) {
		this.name = name;
		this.available = available;
		this.description = description;
		this.arguments = arguments;
		this.permissions = permissions;
	}
}