package com.akjostudios.acsp.backend;

import io.github.akjo03.lib.config.AkjoLibSpringAutoConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
@ImportAutoConfiguration(classes = {
		AkjoLibSpringAutoConfiguration.class
})
@SuppressWarnings("unused")
public class AcspBackend {
	public static void main(String[] args) {
		SpringApplication.run(AcspBackend.class, args);
	}
}