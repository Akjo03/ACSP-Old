package com.akjostudios.acsp.bot;

import com.akjostudios.acsp.bot.constants.BotDeployMode;
import com.akjostudios.acsp.bot.controller.BeginController;
import com.akjostudios.acsp.bot.handlers.BeginChannelHandler;
import com.akjostudios.acsp.bot.handlers.CommandsHandler;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import com.akjostudios.acsp.bot.services.bot.BotConfigService;
import com.akjostudios.acsp.bot.services.bot.BotStringsService;
import com.akjostudios.acsp.bot.services.command.BotCommandArgumentParserService;
import com.akjostudios.acsp.bot.services.command.BotCommandArgumentParsingReportService;
import com.akjostudios.acsp.bot.services.command.CommandHelperService;
import com.akjostudios.acsp.bot.util.command.BotCommand;
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
	private static BotDeployMode botDeployMode;
	@Getter
	private static String botName;

	private final LoggerHandler loggerHandler;
	private final BotConfigService botConfigService;

	public static void main(String[] args) {
		try {
			SpringApplication.run(AcspBot.class, args);
		} catch (Exception e) {
			LOGGER.error("An error occurred while starting the bot: " + e.getMessage());
			shutdown();
		}
	}

	@Override
	public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
		applicationContext = event.getApplicationContext();

		loggerHandler.initialize(event.getApplicationContext());

		try {
			botDeployMode = BotDeployMode.getDeployMode(System.getenv("ACSP_DEPLOY_MODE"));
		} catch (Exception e) {
			LOGGER.error("An error occurred while retrieving the deploy mode: " + e.getMessage());
			shutdown();
		}

		try {
			botConfigService.loadBotConfig();
		} catch (Exception e) {
			LOGGER.error("An error occurred while loading the bot configuration: " + e.getMessage());
			shutdown();
		}

		try {
			jdaInstance = JDABuilder.create(
					System.getenv("ACSP_TOKEN"),
					GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)
			).build();
		} catch (Exception e) {
			LOGGER.error("An error occurred while creating the JDA instance: " + e.getMessage());
			shutdown();
		}

		CommandsHandler.setAvailableCommands(
				applicationContext.getBeansOfType(BotCommand.class).values().stream().toList()
		);

		jdaInstance.addEventListener(applicationContext.getBean(CommandsHandler.class).setup(jdaInstance));

		try { jdaInstance.awaitReady(); } catch (Exception e) { shutdown(); }
		botName = jdaInstance.getSelfUser().getName();

		CommandsHandler.getAvailableCommands().forEach(command -> {
			command.setupServices(
					applicationContext.getBean(BotConfigService.class),
					applicationContext.getBean(DiscordMessageService.class),
					applicationContext.getBean(ErrorMessageService.class),
					applicationContext.getBean(BotCommandArgumentParserService.class),
					applicationContext.getBean(BotCommandArgumentParsingReportService.class),
					applicationContext.getBean(CommandHelperService.class),
					applicationContext.getBean(BotStringsService.class)
			);
			try { command.initializeInternal(applicationContext, jdaInstance); } catch (Exception e) {
				LOGGER.error("An error occurred while initializing the command " + command.getName() + ": " + e.getMessage());
				shutdown();
			}
		});

		jdaInstance.addEventListener(applicationContext.getBean(BeginChannelHandler.class).setup(jdaInstance));

		applicationContext.getBean(BeginController.class).setup(jdaInstance);

		LOGGER.success("AcspBot has successfully started in " + botDeployMode + " mode.");
	}

	@PreDestroy
	public static void shutdown() {
		boolean shutdownFailed = false;

		try { jdaInstance.shutdownNow(); } catch (Exception ignored) {
			shutdownFailed = true;
		} finally { applicationContext.close(); }

		if (shutdownFailed) {
			Runtime.getRuntime().halt(1);
		}
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