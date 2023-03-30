package com.akjostudios.acsp.bot.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
	private final ExternalServiceConfig externalServiceConfig;

	@Value("${server.ssl.trust-store-password}")
	private String trustStorePassword;

	@Bean
	public WebClient webClient() throws Exception {
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new ClassPathResource("certs/truststore.jks").getInputStream(), trustStorePassword.toCharArray());

		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(keyStore);

		SslContext sslContext = SslContextBuilder
				.forClient()
				.trustManager(trustManagerFactory)
				.build();

		HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
		return WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.baseUrl(externalServiceConfig.getBackendBaseUrl())
				.build();
	}
}