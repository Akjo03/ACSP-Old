package com.akjostudios.acsp.bot.util.handlers;

import net.dv8tion.jda.api.JDA;

public interface AcspBotHandler<T extends AcspBotHandler<T>> {
	T setup(JDA jdaInstance);
}