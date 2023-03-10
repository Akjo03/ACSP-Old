package com.akjostudios.acsp.bot;

import io.github.akjo03.lib.config.AkjoLibSpringAutoConfiguration;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerHandler;
import io.github.akjo03.lib.logging.LoggerManager;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
@ImportAutoConfiguration(classes = {
		AkjoLibSpringAutoConfiguration.class
})
@SuppressWarnings("unused")
public class AcspBot implements ApplicationListener<ApplicationReadyEvent> {
	private static final Logger LOGGER = LoggerManager.getLogger(AcspBot.class);

	private static ConfigurableApplicationContext applicationContext;
	private static JDA jdaInstance;

	private final LoggerHandler loggerHandler;

	public static void main(String[] args) {
		try {
			SpringApplication.run(AcspBot.class, args);
		} catch (Exception e) {
			LOGGER.error("Failed to start application AcspBot!", e);
			System.exit(1);
		}
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		loggerHandler.initialize(event.getApplicationContext());
		applicationContext = event.getApplicationContext();

		jdaInstance = JDABuilder.create(
				System.getenv("ACSP_TOKEN"),
				GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)
		).build();

		try { jdaInstance.awaitReady(); } catch (Exception e) { shutdown(); }

		LOGGER.success("AcspBot has successfully started!");
	}

	@PreDestroy
	public static void shutdown() {
		LOGGER.info("AcspBot is shutting down...");
		jdaInstance.shutdownNow();
		applicationContext.close();
		Runtime.getRuntime().halt(0);
	}

	public static void restart() {
		LOGGER.info("AcspBackend is restarting...");
		ApplicationArguments args = applicationContext.getBean(ApplicationArguments.class);
		Thread restartThread = new Thread(() -> {
			shutdown();
			applicationContext = SpringApplication.run(AcspBot.class, args.getSourceArgs());
		});
		restartThread.setDaemon(false);
		restartThread.start();
	}
}