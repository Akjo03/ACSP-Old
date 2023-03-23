package com.akjostudios.acsp.bot.util.exception;

import com.akjostudios.acsp.bot.AcspBot;
import com.akjostudios.acsp.bot.constants.BotConstants;
import com.akjostudios.acsp.bot.constants.BotLanguages;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.bot.BotConfigService;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Getter
public class AcspBotCommandArgumentParseException extends AcspBotException {
	private final String argumentName;
	private final String reasonLabel;
	private final List<String> reasonPlaceholders;

	public AcspBotCommandArgumentParseException(
			String argumentName,
			String reasonLabel,
			List<String> reasonPlaceholders,
			BotLanguages language,
			DiscordMessageService discordMessageService,
			BotConfigService botConfigService
	) {
		super(botConfigService.getMessageDefinition(
				"ARGUMENT_PARSE_ERROR",
				Optional.ofNullable(language),
				List.of(
						AcspBot.getBotName(),
						BotConstants.DATE_TIME_FORMATTER.format(Instant.now())
				).toArray(String[]::new)
		), discordMessageService);

		this.argumentName = argumentName;
		this.reasonLabel = reasonLabel;
		this.reasonPlaceholders = reasonPlaceholders;
	}
}