package com.akjostudios.acsp.bot;

import com.akjostudios.acsp.bot.constants.DeployMode;
import com.akjostudios.acsp.bot.services.BotConfigService;
import io.github.akjo03.lib.config.AkjoLibSpringAutoConfiguration;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerHandler;
import io.github.akjo03.lib.logging.LoggerManager;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
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
public class AcspBot implements ApplicationListener<ApplicationReadyEvent> {
	private static final Logger LOGGER = LoggerManager.getLogger(AcspBot.class);

	private static ConfigurableApplicationContext applicationContext;
	private static JDA jdaInstance;

	@Getter
	private static DeployMode deployMode;
	@Getter
	private static String botName;

	private final LoggerHandler loggerHandler;
	private final BotConfigService botConfigService;

	public static void main(String[] args) {
		SpringApplication.run(AcspBot.class, args);
	}

	@Override
	public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
		applicationContext = event.getApplicationContext();
		loggerHandler.initialize(event.getApplicationContext());
		deployMode = DeployMode.getDeployMode(System.getenv("ACSP_DEPLOY_MODE"));
		botConfigService.loadBotConfig();

		jdaInstance = JDABuilder.create(
				System.getenv("ACSP_TOKEN"),
				GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)
		).build();
		try { jdaInstance.awaitReady(); } catch (Exception e) { shutdown(); }

		botName = jdaInstance.getSelfUser().getName();

		LOGGER.success("AcspBot has successfully started in " + deployMode + " mode.");
	}

	@PreDestroy
	public static void shutdown() {
		boolean shutdownFailed = false;

		try { jdaInstance.shutdownNow(); } catch (Exception ignored) {
			shutdownFailed = true;
		} finally { applicationContext.close(); }

		if (shutdownFailed) { Runtime.getRuntime().halt(1); }
	}

	public static void restart() {
		ApplicationArguments args = applicationContext.getBean(ApplicationArguments.class);
		Thread restartThread = new Thread(() -> {
			shutdown();
			applicationContext = SpringApplication.run(AcspBot.class, args.getSourceArgs());
		});
		restartThread.setDaemon(false);
		restartThread.start();
	}
}