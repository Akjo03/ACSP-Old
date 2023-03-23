package com.akjostudios.acsp.bot.commands;

import com.akjostudios.acsp.bot.AcspBot;
import com.akjostudios.acsp.bot.constants.BotConstants;
import com.akjostudios.acsp.bot.dto.BeginAuthResponseDto;
import com.akjostudios.acsp.bot.services.DiscordMessageService;
import com.akjostudios.acsp.bot.services.ErrorMessageService;
import com.akjostudios.acsp.bot.services.bot.BotConfigService;
import com.akjostudios.acsp.bot.util.command.BotCommand;
import com.akjostudios.acsp.bot.util.command.BotCommandInitializer;
import com.akjostudios.acsp.bot.util.command.argument.BotCommandArguments;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
public class BeginCommand extends BotCommand {
	private static final Logger LOGGER = LoggerManager.getLogger(BeginCommand.class);

	@Value("${application.services.backend-base-url}")
	private String backendBaseUrl;

	@Value("${application.secrets.acsp-begin-secret}")
	private String acspBeginSecret;

	private WebClient webClient;
	private DiscordMessageService discordMessageService;
	private ErrorMessageService errorMessageService;
	private BotConfigService botConfigService;

	protected BeginCommand() {
		super("begin");
	}

	@Override
	public void initialize(@NotNull BotCommandInitializer initializer) {
		this.webClient = initializer.getBean(WebClient.Builder.class)
				.baseUrl(backendBaseUrl)
				.build();
		this.discordMessageService = initializer.getBean(DiscordMessageService.class);
		this.errorMessageService = initializer.getBean(ErrorMessageService.class);
		this.botConfigService = initializer.getBean(BotConfigService.class);
	}

	@Override
	@SuppressWarnings("CodeBlock2Expr")
	public void execute(@NotNull MessageReceivedEvent event, BotCommandArguments arguments) {
		LOGGER.info("Executing begin command...");

		BeginAuthResponseDto beginAuthResponseDto = null;
		try {
			beginAuthResponseDto = webClient.get()
					.uri("/api/auth/begin?userId=" + event.getAuthor().getId() + "&secret=" + acspBeginSecret + "&messageId=" + event.getMessageId())
					.retrieve()
					.onStatus(httpStatus -> httpStatus.value() == HttpStatus.ALREADY_REPORTED.value(), clientResponse -> {
						event.getAuthor().openPrivateChannel().queue(privateChannel -> {
							privateChannel.sendMessage(discordMessageService.createMessage(
									errorMessageService.getErrorMessage(
											"errors.command.begin.already_linked.title",
											"errors.command.begin.already_linked.description",
											List.of(),
											List.of(),
											Optional.empty()
									)
							)).queue();
						});
						return null;
					})
					.onStatus(HttpStatusCode::isError, clientResponse -> {
						event.getAuthor().openPrivateChannel().queue(privateChannel -> {
							privateChannel.sendMessage(discordMessageService.createMessage(
									errorMessageService.getErrorMessage(
											"errors.command.begin.link_generation_failed.title",
											"errors.command.begin.link_generation_failed.description",
											List.of(),
											List.of(),
											Optional.empty()
									)
							)).queue();
						});
						return null;
					}).bodyToMono(BeginAuthResponseDto.class).block();
		} catch (Exception ignored) {}
		if (beginAuthResponseDto == null) {
			return;
		}

		BeginAuthResponseDto finalBeginAuthResponseDto = beginAuthResponseDto;
		event.getAuthor().openPrivateChannel().queue(privateChannel -> {
			privateChannel.sendMessage(discordMessageService.createMessage(
					botConfigService.getMessageDefinition(
							"BEGIN_LINK_MESSAGE",
							Optional.empty(),
							finalBeginAuthResponseDto.getAuthLink(),
							AcspBot.getBotName(),
							BotConstants.DATE_TIME_FORMATTER.format(Instant.now())
					)
			)).queue();
		});
	}
}