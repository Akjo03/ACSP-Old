package com.akjostudios.acsp.bot.services;

import com.akjostudios.acsp.bot.config.LocaleConfiguration;
import com.akjostudios.acsp.bot.constants.Languages;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BotStringsService {
	private final StringPlaceholderService stringPlaceholderService;
	private final ResourceBundleMessageSource resourceBundleMessageSource;
	private final LocaleConfiguration localeConfiguration;

	public String getString(String label, Optional<Languages> language, String... placeholders) {
		return stringPlaceholderService.replacePlaceholders(
				resourceBundleMessageSource.getMessage(label, null, getLocale(language)),
				placeholders
		);
	}

	public Locale getLocale(Optional<Languages> language) {
		return language.orElse(Languages.fromCode(localeConfiguration.getDefaultLocale())).getLocale();
	}
}