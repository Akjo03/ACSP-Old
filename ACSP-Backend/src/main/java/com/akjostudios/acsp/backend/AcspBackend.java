package com.akjostudios.acsp.backend;

import com.akjostudios.acsp.backend.util.DbInitializer;
import io.github.akjo03.lib.config.AkjoLibSpringAutoConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
@RequiredArgsConstructor
@ImportAutoConfiguration(classes = {
		AkjoLibSpringAutoConfiguration.class
})
@SuppressWarnings("unused")
public class AcspBackend implements ApplicationListener<ApplicationReadyEvent> {
	public static void main(String[] args) {
		SpringApplication.run(AcspBackend.class, args);
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		event.getApplicationContext()
				.getBeansOfType(DbInitializer.class)
				.forEach((s, dbInitializer) -> dbInitializer.init());
	}
}