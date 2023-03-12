package com.akjostudios.acsp.bot.util.command.argument.serialization;

import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentData;
import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentIntegerData;
import com.akjostudios.acsp.bot.config.bot.command.argument.data.BotConfigCommandArgumentStringData;
import com.akjostudios.acsp.bot.constants.CommandArgumentTypes;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BotConfigCommandArgumentDeserializer extends StdDeserializer<BotConfigCommandArgument<?>> {
	public BotConfigCommandArgumentDeserializer() {
		this(null);
	}

	public BotConfigCommandArgumentDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public BotConfigCommandArgument<?> deserialize(@NotNull JsonParser jsonParser, @NotNull DeserializationContext deserializationContext) throws IOException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		String name = node.get("name").asText();
		String type = node.get("type").asText();
		String description = node.get("description").asText();
		boolean required = node.get("required").asBoolean();
		JsonNode dataNode = node.get("data");

		CommandArgumentTypes argumentType = CommandArgumentTypes.fromString(type);
		if (argumentType == null) {
			throw MismatchedInputException.from(jsonParser, BotConfigCommandArgument.class, "Invalid argument type: " + type + "!");
		}
		BotConfigCommandArgumentData<?> data = getDataDefinition(jsonParser, dataNode, argumentType);
		return new BotConfigCommandArgument<>(name, type, description, required, data);
	}

	private BotConfigCommandArgumentData<?> getDataDefinition(@NotNull JsonParser jsonParser, @NotNull JsonNode dataNode, @NotNull CommandArgumentTypes type) throws IOException {
		return switch (type) {
			case INTEGER -> jsonParser.getCodec().treeToValue(dataNode, BotConfigCommandArgumentIntegerData.class);
			case STRING -> jsonParser.getCodec().treeToValue(dataNode, BotConfigCommandArgumentStringData.class);
		};
	}
}