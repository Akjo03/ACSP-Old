package com.akjostudios.acsp.bot.commands;

import com.akjostudios.acsp.bot.AcspBot;
import com.akjostudios.acsp.bot.constants.BotConstants;
import com.akjostudios.acsp.bot.dto.BeginLinkResponseDto;
import com.akjostudios.acsp.bot.dto.UserSessionStatusDto;
import com.akjostudios.acsp.bot.model.AcspUserSessionStatus;
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

	@Value("${application.secrets.acsp-bot-api-secret}")
	private String acspBotApiSecret;

	private WebClient webClient;
	private DiscordMessageService discordMessageService;
	private ErrorMessageService errorMessageService;
	private BotConfigService botConfigService;

	protected BeginCommand() {
		super("begin");
	}

	@Override
	public void initialize(@NotNull BotCommandInitializer initializer) {
		this.webClient = initializer.getBean(WebClient.class);
		this.discordMessageService = initializer.getBean(DiscordMessageService.class);
		this.errorMessageService = initializer.getBean(ErrorMessageService.class);
		this.botConfigService = initializer.getBean(BotConfigService.class);
	}

	@Override
	@SuppressWarnings("CodeBlock2Expr")
	public void execute(@NotNull MessageReceivedEvent event, BotCommandArguments arguments) {
		LOGGER.info("Executing begin command...");

		UserSessionStatusDto userSessionStatusDto = null;
		try {
			userSessionStatusDto = webClient.get()
					.uri("/api/user/session/status?userId=" + event.getAuthor().getId())
					.retrieve()
					.bodyToMono(UserSessionStatusDto.class).block();
		} catch (Exception ignored) {}
		LOGGER.info("User session status: " + userSessionStatusDto);

		BeginLinkResponseDto beginLinkResponseDto = null;
		try {
			UserSessionStatusDto finalUserSessionStatusDto = userSessionStatusDto;
			beginLinkResponseDto = webClient.get()
					.uri("/api/auth/begin?userId=" + event.getAuthor().getId() + "&secret=" + acspBeginSecret + "&messageId=" + event.getMessageId())
					.retrieve()
					.onStatus(httpStatus -> httpStatus.value() == HttpStatus.ALREADY_REPORTED.value(), clientResponse -> {
						if (finalUserSessionStatusDto == null) {
							return null;
						}
						if (finalUserSessionStatusDto.getSessionStatus().equals(AcspUserSessionStatus.ONBOARDING.getStatus())) {
							event.getAuthor().openPrivateChannel().queue(privateChannel -> {
								privateChannel.sendMessage(discordMessageService.createMessage(
										errorMessageService.getErrorMessage(
												"errors.command.begin.onboarding_in_progress.title",
												"errors.command.begin.onboarding_in_progress.description",
												List.of(),
												List.of(
														backendBaseUrl + "/api/user/onboarding?userId=" + event.getAuthor().getId() + "&secret=" + acspBeginSecret
												),
												Optional.empty()
										)
								)).queue();
							});
						} else {
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
						}
						return null;
					})
					.onStatus(httpStatus -> httpStatus.value() == HttpStatus.GONE.value(), clientResponse -> {
						event.getAuthor().openPrivateChannel().queue(privateChannel -> {
							privateChannel.sendMessage(discordMessageService.createMessage(
									errorMessageService.getErrorMessage(
											"errors.command.begin.user_deleted.title",
											"errors.command.begin.user_deleted.description",
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
					}).bodyToMono(BeginLinkResponseDto.class).block();
		} catch (Exception ignored) {}
		if (beginLinkResponseDto == null) {
			return;
		}

		BeginLinkResponseDto finalBeginAuthResponseDto = beginLinkResponseDto;
		event.getAuthor().openPrivateChannel().queue(privateChannel -> {
			privateChannel.sendMessage(discordMessageService.createMessage(
					botConfigService.getMessageDefinition(
							"BEGIN_LINK_MESSAGE",
							Optional.empty(),
							finalBeginAuthResponseDto.getBeginLink(),
							AcspBot.getBotName(),
							BotConstants.DATE_TIME_FORMATTER.format(Instant.now())
					)
			)).queue();
		});
	}
}