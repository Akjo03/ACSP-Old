package com.akjostudios.acsp.bot.services;

import com.akjostudios.acsp.bot.AcspBot;
import com.akjostudios.acsp.bot.config.bot.message.BotConfigMessage;
import com.akjostudios.acsp.bot.constants.BotConstants;
import com.akjostudios.acsp.bot.constants.BotLanguages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ErrorMessageService {
	private final BotConfigService botConfigService;
	private final BotStringsService botStringsService;

	public BotConfigMessage getErrorMessage(
			String titleLabel,
			String descriptionLabel,
			List<String> titlePlaceholders,
			List<String> descriptionPlaceholders,
			Optional<BotLanguages> language
	) {
		return botConfigService.getMessageDefinition(
				"ERROR_MESSAGE", language,
				botStringsService.getString(titleLabel, language, titlePlaceholders.toArray(String[]::new)),
				botStringsService.getString(descriptionLabel, language, descriptionPlaceholders.toArray(String[]::new)),
				AcspBot.getBotName(),
				BotConstants.DATE_TIME_FORMATTER.format(Instant.now())
		);
	}

	public BotConfigMessage getInternalErrorMessage(
			String message,
			Optional<BotLanguages> language
	) {
		return botConfigService.getMessageDefinition(
				"INTERNAL_ERROR_MESSAGE", language,
				message,
				AcspBot.getBotName(),
				BotConstants.DATE_TIME_FORMATTER.format(Instant.now())
		);
	}
}