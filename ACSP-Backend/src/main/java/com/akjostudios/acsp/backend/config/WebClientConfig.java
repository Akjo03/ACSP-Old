package com.akjostudios.acsp.backend.config;

import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
	private static final Logger LOGGER = LoggerManager.getLogger(WebClientConfig.class);

	private final ApplicationConfig applicationConfig;
	private final WebClient.Builder webClientBuilder;

	@Value("${server.ssl.trust-store-password}")
	private String trustStorePassword;

	@Bean
	@Qualifier("discordTokenClient")
	public WebClient discordTokenClient() {
		return webClientBuilder
				.baseUrl("https://discord.com/api/oauth2/token").build();
	}

	@Bean
	@Qualifier("discordApiClient")
	public WebClient discordApiClient() {
		return webClientBuilder
				.baseUrl("https://discord.com/api").build();
	}

	@Bean
	@Qualifier("discordBotClient")
	public WebClient discordBotClient() {
		return webClientBuilder
				.clientConnector(new ReactorClientHttpConnector(getHttpClient().orElseThrow()))
				.baseUrl(applicationConfig.getDiscordBotUrl()).build();
	}

	@Bean
	@Qualifier("selfClient")
	public WebClient selfClient() {
		return webClientBuilder
				.clientConnector(new ReactorClientHttpConnector(getHttpClient().orElseThrow()))
				.baseUrl(applicationConfig.getBaseUrl() + "/api").build();
	}

	private Optional<HttpClient> getHttpClient() {
		try {
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new ClassPathResource("certs/truststore.jks").getInputStream(), trustStorePassword.toCharArray());

			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(keyStore);

			SslContext sslContext = SslContextBuilder
					.forClient()
					.trustManager(trustManagerFactory)
					.build();

			return Optional.of(HttpClient.create().secure(t -> t.sslContext(sslContext)));
		} catch (Exception e) {
			LOGGER.error("Failed to get http client!", e);
			return Optional.empty();
		}
	}
}
