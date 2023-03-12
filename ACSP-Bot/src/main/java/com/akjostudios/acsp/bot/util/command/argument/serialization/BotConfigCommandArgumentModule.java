package com.akjostudios.acsp.bot.util.command.argument.serialization;

import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class BotConfigCommandArgumentModule extends SimpleModule {
	public BotConfigCommandArgumentModule() {
		addDeserializer(BotConfigCommandArgument.class, new BotConfigCommandArgumentDeserializer());
	}
}