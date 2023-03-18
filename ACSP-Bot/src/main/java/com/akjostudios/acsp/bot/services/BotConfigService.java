package com.akjostudios.acsp.bot.services;

import com.akjostudios.acsp.bot.AcspBot;
import com.akjostudios.acsp.bot.config.LocaleConfiguration;
import com.akjostudios.acsp.bot.config.bot.BotConfig;
import com.akjostudios.acsp.bot.config.bot.command.BotConfigCommand;
import com.akjostudios.acsp.bot.config.bot.command.BotConfigSubcommand;
import com.akjostudios.acsp.bot.config.bot.command.argument.BotConfigCommandArgument;
import com.akjostudios.acsp.bot.config.bot.field.BotConfigFieldWrapper;
import com.akjostudios.acsp.bot.config.bot.message.BotConfigMessage;
import com.akjostudios.acsp.bot.config.bot.message.BotConfigMessageWrapper;
import com.akjostudios.acsp.bot.config.bot.message.embed.BotConfigMessageEmbed;
import com.akjostudios.acsp.bot.config.bot.message.embed.BotConfigMessageEmbedAuthor;
import com.akjostudios.acsp.bot.config.bot.message.embed.BotConfigMessageEmbedField;
import com.akjostudios.acsp.bot.config.bot.message.embed.BotConfigMessageEmbedFooter;
import com.akjostudios.acsp.bot.constants.BotLanguages;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import io.github.akjo03.lib.path.ProjectDirectory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BotConfigService {
	private static final Logger LOGGER = LoggerManager.getLogger(BotConfigService.class);

	private Path botConfigPath;
	private BotConfig botConfig;

	private final JsonService jsonService;
	private final ProjectDirectory projectDirectory;
	private final LocaleConfiguration localeConfiguration;
	private final StringPlaceholderService stringPlaceholderService;
	private final BotStringsService botStringsService;

	public void loadBotConfig() {
		if (botConfigPath == null) {
			botConfigPath = projectDirectory.getProjectRootDirectory().resolve("bot_config.json");
		}

		try { botConfig = jsonService.objectMapper().readValue(botConfigPath.toFile(), BotConfig.class); } catch (Exception e) {
			LOGGER.error("Failed to load bot config file!", e);
			AcspBot.shutdown();
		}
	}

	public @Nullable BotConfigMessage getMessageDefinition(@NotNull String label, @NotNull Optional<BotLanguages> language, String@NotNull... placeholders) {
		loadBotConfig();

		BotConfigMessageWrapper messageWrapper = botConfig.getMessages().stream()
				.filter(message -> message.getLabel().equals(label))
				.filter(message -> message.getLanguage().equals(localeConfiguration.getLanguage(language).getCode()))
				.findFirst().orElse(null);
		if (messageWrapper == null) {
			LOGGER.error("Could not find message with label " + label + " and language " + language + "!");
			return null;
		}

		BotConfigMessage result = new BotConfigMessage();
		result.setContent(stringPlaceholderService.replacePlaceholders(
				messageWrapper.getMessage().getContent(),
				placeholders
		));
		for (BotConfigMessageEmbed embed : messageWrapper.getMessage().getEmbeds()) {
			BotConfigMessageEmbed resultEmbed = new BotConfigMessageEmbed();
			resultEmbed.setAuthor(new BotConfigMessageEmbedAuthor(
					stringPlaceholderService.replacePlaceholders(embed.getAuthor().getName(), placeholders),
					stringPlaceholderService.replacePlaceholders(embed.getAuthor().getUrl(), placeholders),
					stringPlaceholderService.replacePlaceholders(embed.getAuthor().getIconUrl(), placeholders)
			));
			resultEmbed.setTitle(stringPlaceholderService.replacePlaceholders(embed.getTitle(), placeholders));
			resultEmbed.setDescription(stringPlaceholderService.replacePlaceholders(embed.getDescription(), placeholders));
			resultEmbed.setUrl(stringPlaceholderService.replacePlaceholders(embed.getUrl(), placeholders));
			resultEmbed.setColor(stringPlaceholderService.replacePlaceholders(embed.getColor(), placeholders));
			for (BotConfigMessageEmbedField field : embed.getFields()) {
				BotConfigMessageEmbedField resultField = new BotConfigMessageEmbedField();
				resultField.setName(stringPlaceholderService.replacePlaceholders(field.getName(), placeholders));
				resultField.setValue(stringPlaceholderService.replacePlaceholders(field.getValue(), placeholders));
				resultField.setInline(field.isInline());
				resultEmbed.getFields().add(resultField);
			}
			resultEmbed.setImageUrl(stringPlaceholderService.replacePlaceholders(embed.getImageUrl(), placeholders));
			resultEmbed.setThumbnailUrl(stringPlaceholderService.replacePlaceholders(embed.getThumbnailUrl(), placeholders));
			resultEmbed.setFooter(new BotConfigMessageEmbedFooter(
					stringPlaceholderService.replacePlaceholders(embed.getFooter().getText(), placeholders),
					stringPlaceholderService.replacePlaceholders(embed.getFooter().getTimestamp(), placeholders),
					stringPlaceholderService.replacePlaceholders(embed.getFooter().getIconUrl(), placeholders)
			));
			result.getEmbeds().add(resultEmbed);
		}
		return result;
	}

	public @Nullable BotConfigMessageEmbedField getFieldDefinition(@NotNull String label, @NotNull Optional<BotLanguages> language, String@NotNull... placeholders) {
		loadBotConfig();

		BotConfigFieldWrapper fieldWrapper = botConfig.getFields().stream()
				.filter(field -> field.getLabel().equals(label))
				.filter(field -> field.getLanguage().equals(localeConfiguration.getLanguage(language).getCode()))
				.findFirst().orElse(null);
		if (fieldWrapper == null) {
			LOGGER.error("Could not find field with label " + label + " and language " + language + "!");
			return null;
		}

		BotConfigMessageEmbedField result = new BotConfigMessageEmbedField();
		result.setName(stringPlaceholderService.replacePlaceholders(fieldWrapper.getField().getName(), placeholders));
		result.setValue(stringPlaceholderService.replacePlaceholders(fieldWrapper.getField().getValue(), placeholders));
		result.setInline(fieldWrapper.getField().isInline());
		return result;
	}

	public @Nullable BotConfigCommand getCommandDefinition(@NotNull String name, @NotNull Optional<BotLanguages> language, String@NotNull... placeholders) {
		loadBotConfig();

		BotConfigCommand commandDefinition = botConfig.getCommands().stream()
				.filter(command -> command.getName().equals(name))
				.findFirst().orElse(null);
		if (commandDefinition == null) {
			LOGGER.error("Could not find command with name " + name + "!");
			return null;
		}

		BotConfigCommand result = new BotConfigCommand();
		result.setName(commandDefinition.getName());
		result.setDescription(replaceString(commandDefinition.getDescription(), language, placeholders));
		result.setArguments(commandDefinition.getArguments());
		result.setSubcommands(commandDefinition.getSubcommands());

		for (BotConfigCommandArgument<?> argument : result.getArguments()) {
			argument.setDescription(replaceString(argument.getDescription(), language, placeholders));
		}
		if (commandDefinition.getSubcommands().isAvailable()) {
			for (BotConfigSubcommand subcommand : result.getSubcommands().getDefinitions()) {
				subcommand.setDescription(replaceString(subcommand.getDescription(), language, placeholders));
				for (BotConfigCommandArgument<?> argument : subcommand.getArguments()) {
					argument.setDescription(replaceString(argument.getDescription(), language, placeholders));
				}
			}
		}

		return result;
	}

	public String getCommandPrefix() {
		loadBotConfig();
		return botConfig.getCommandPrefix();
	}

	private @NotNull String replaceString(@NotNull String strName, @NotNull Optional<BotLanguages> strLanguage, String@NotNull... strPlaceholders) {
		if (!strName.startsWith("$")) {
			return strName;
		}
		String strLabel = strName.substring(1);

		String str = botStringsService.getString(strLabel, strLanguage, strPlaceholders);
		if (str == null) {
			return strName;
		}

		return str;
	}
}