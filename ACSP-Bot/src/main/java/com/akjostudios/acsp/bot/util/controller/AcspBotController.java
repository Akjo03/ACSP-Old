package com.akjostudios.acsp.bot.util.controller;

import net.dv8tion.jda.api.JDA;

public abstract class AcspBotController {
	protected JDA jdaInstance;

	public void setup(JDA jdaInstance) {
		this.jdaInstance = jdaInstance;
	}
}