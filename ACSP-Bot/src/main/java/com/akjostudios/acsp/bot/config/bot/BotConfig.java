package com.akjostudios.acsp.bot.config.bot;

import com.akjostudios.acsp.bot.config.bot.command.BotConfigCommand;
import com.akjostudios.acsp.bot.config.bot.components.BotConfigComponentWrapper;
import com.akjostudios.acsp.bot.config.bot.field.BotConfigFieldWrapper;
import com.akjostudios.acsp.bot.config.bot.message.BotConfigMessageWrapper;
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
public class BotConfig {
	@JsonSerialize
	@JsonDeserialize
	private List<BotConfigMessageWrapper> messages;

	@JsonSerialize
	@JsonDeserialize
	private List<BotConfigFieldWrapper> fields;

	@JsonSerialize
	@JsonDeserialize
	private List<BotConfigComponentWrapper> components;

	@JsonSerialize
	@JsonDeserialize
	private List<BotConfigCommand> commands;

	@JsonSerialize
	@JsonDeserialize
	private String commandPrefix;

	public BotConfig(
			@JsonProperty("messages") List<BotConfigMessageWrapper> messages,
			@JsonProperty("fields") List<BotConfigFieldWrapper> fields,
			@JsonProperty("components") List<BotConfigComponentWrapper> components,
			@JsonProperty("commands") List<BotConfigCommand> commands,
			@JsonProperty("command_prefix") String commandPrefix
	) {
		this.messages = messages;
		this.fields = fields;
		this.components = components;
		this.commands = commands;
		this.commandPrefix = commandPrefix;
	}
}