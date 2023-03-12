package com.akjostudios.acsp.bot.services;

import com.akjostudios.acsp.bot.AcspBot;
import com.akjostudios.acsp.bot.config.LocaleConfiguration;
import com.akjostudios.acsp.bot.config.bot.BotConfig;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import io.github.akjo03.lib.path.ProjectDirectory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class BotConfigService {
	private static final Logger LOGGER = LoggerManager.getLogger(BotConfigService.class);

	@Getter
	private Path botConfigPath;
	@Getter
	private BotConfig botConfig;

	private final JsonService jsonService;
	private final ProjectDirectory projectDirectory;

	private final LocaleConfiguration localeConfiguration;

	public void loadBotConfig() {
		if (botConfigPath == null) {
			botConfigPath = projectDirectory.getProjectRootDirectory().resolve("bot_config.json");
		}

		try { botConfig = jsonService.objectMapper().readValue(botConfigPath.toFile(), BotConfig.class); } catch (Exception e) {
			LOGGER.error("Failed to load bot config file!", e);
			AcspBot.shutdown();
		}
	}
}