package com.akjostudios.acsp.bot.util.command;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
public class BotCommandInitializer {
	private final ApplicationContext applicationContext;
	private final JDA jdaInstance;

	private BotCommandInitializer(ApplicationContext applicationContext, JDA jdaInstance) {
		this.applicationContext = applicationContext;
		this.jdaInstance = jdaInstance;
	}

	@Contract(value = "_, _ -> new", pure = true)
	public static @NotNull BotCommandInitializer of(ApplicationContext applicationContext, JDA jdaInstance) {
		return new BotCommandInitializer(applicationContext, jdaInstance);
	}

	public <T> T getBean(Class<T> beanClass) {
		return applicationContext.getBean(beanClass);
	}
}