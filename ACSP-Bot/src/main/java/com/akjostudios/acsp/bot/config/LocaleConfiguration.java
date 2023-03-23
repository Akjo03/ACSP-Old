package com.akjostudios.acsp.bot.config;

import com.akjostudios.acsp.bot.constants.BotLanguages;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Optional;

@Configuration
@Getter
public class LocaleConfiguration {
	@Value("${application.locale.resource-name}")
	private String resourceName;

	@Value("${application.locale.default}")
	private String defaultLocale;

	@Bean(name = "stringsResourceBundleMessageSource")
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource rs = new ResourceBundleMessageSource();
		rs.setBasename(resourceName);
		rs.setDefaultEncoding("UTF-8");
		rs.setUseCodeAsDefaultMessage(true);
		return rs;
	}

	public BotLanguages getLanguage(Optional<BotLanguages> language) {
		return language.orElse(BotLanguages.fromCode(defaultLocale));
	}
}