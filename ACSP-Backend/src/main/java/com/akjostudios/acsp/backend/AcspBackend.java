package com.akjostudios.acsp.backend;

import io.github.akjo03.lib.config.AkjoLibSpringAutoConfiguration;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerHandler;
import io.github.akjo03.lib.logging.LoggerManager;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@RequiredArgsConstructor
@ImportAutoConfiguration(classes = {
		AkjoLibSpringAutoConfiguration.class
})
@SuppressWarnings("unused")
public class AcspBackend implements ApplicationListener<ApplicationReadyEvent> {
	private static final Logger LOGGER = LoggerManager.getLogger(AcspBackend.class);

	private static ConfigurableApplicationContext applicationContext;

	private final LoggerHandler loggerHandler;

	public static void main(String[] args) {
		try {
			SpringApplication.run(AcspBackend.class, args);
		} catch (Exception e) {
			LOGGER.error("Failed to start application AcspBackend!", e);
			System.exit(1);
		}
	}

	@Override
	public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
		loggerHandler.initialize(event.getApplicationContext());
		applicationContext = event.getApplicationContext();
		LOGGER.success("AcspBackend has successfully started!");
	}

	@PreDestroy
	public static void shutdown() {
		LOGGER.info("AcspBackend is shutting down...");
		applicationContext.close();
		Runtime.getRuntime().halt(0);
	}

	public static void restart() {
		LOGGER.info("AcspBackend is restarting...");
		ApplicationArguments args = applicationContext.getBean(ApplicationArguments.class);
		Thread restartThread = new Thread(() -> {
			applicationContext.close();
			applicationContext = SpringApplication.run(AcspBackend.class, args.getSourceArgs());
		});
		restartThread.setDaemon(false);
		restartThread.start();
	}
}