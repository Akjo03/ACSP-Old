package com.akjostudios.acsp.bot.services;

import com.akjostudios.acsp.bot.AcspBot;
import com.akjostudios.acsp.bot.config.bot.message.BotConfigMessage;
import com.akjostudios.acsp.bot.constants.BotConstants;
import com.akjostudios.acsp.bot.util.exception.AcspBotCommandArgumentParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BotCommandArgumentParsingReportService {
	private final BotConfigService botConfigService;
	private final BotStringsService botStringsService;

	public BotConfigMessage getReportMessage(String commandName, List<AcspBotCommandArgumentParseException> parseExceptions) {
		BotConfigMessage reportMessage = botConfigService.getMessageDefinition(
				"ARGUMENT_PARSE_ERROR", Optional.empty(),
				commandName,
				AcspBot.getBotName(),
				BotConstants.DATE_TIME_FORMATTER.format(Instant.now())
		);

		if (reportMessage == null) { return null; }
		if (reportMessage.getEmbeds() == null) { return null; }
		if (reportMessage.getEmbeds().size() != 1) { return null; }

		reportMessage.getEmbeds().get(0).setFields(parseExceptions.stream()
				.map(parseException -> botConfigService.getFieldDefinition(
						"ARGUMENT_PARSE_ERROR_FIELD", Optional.empty(),
						parseException.getArgumentName(),
						botStringsService.getString(
								parseException.getReasonLabel(), Optional.empty(),
								parseException.getReasonPlaceholders().toArray(String[]::new)
						)
				)).toList());

		return reportMessage;
	}
}