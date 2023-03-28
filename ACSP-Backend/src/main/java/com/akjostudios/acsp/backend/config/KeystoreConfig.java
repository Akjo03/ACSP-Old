package com.akjostudios.acsp.backend.config;

import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Configuration
@ConfigurationProperties(prefix = "application.keystore")
@RequiredArgsConstructor
@Getter
@Setter
public class KeystoreConfig {
	private static final Logger LOGGER = LoggerManager.getLogger(KeystoreConfig.class);
	private static final int CERT_EXPIRY = 365 * 30; // 30 years

	private String path;
	private String password;
	private String alias;

	public void generateKeystore() {
		Path keystorePath = Path.of(path);
		LOGGER.success("Generated keystore at " + keystorePath.toAbsolutePath());
		if (Files.exists(keystorePath)) {
			return;
		}

		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();

			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(null, password.toCharArray());

			X509Certificate certificate = (X509Certificate) createSelfSignedCertificate(keyPair);

			X509Certificate[] certificateChain = { certificate };
			keyStore.setKeyEntry(alias, keyPair.getPrivate(), password.toCharArray(), certificateChain);

			try (FileOutputStream fos = new FileOutputStream(keystorePath.toFile())) {
				keyStore.store(fos, password.toCharArray());
			}
		} catch (NoSuchAlgorithmException | KeyStoreException | CertificateException | IOException | OperatorCreationException e) {
			e.printStackTrace();
		}
	}

	private Certificate createSelfSignedCertificate(@NotNull KeyPair keyPair) throws OperatorCreationException, CertificateException, IOException {
		Provider provider = new BouncyCastleProvider();
		Security.addProvider(provider);

		Instant now = Instant.now();
		Instant expiry = now.plus(CERT_EXPIRY, ChronoUnit.DAYS);

		X500Name dnName = new X500NameBuilder()
				.addRDN(BCStyle.C, "CH")
				.addRDN(BCStyle.ST, "Bern")
				.addRDN(BCStyle.CN, "Lukas Küffer")
				.addRDN(BCStyle.O, "AkjoStudios")
				.addRDN(BCStyle.OU, "ACSP")
				.addRDN(BCStyle.E, "lukas.kueffer@outlook.com")
				.build();
		BigInteger serial = BigInteger.valueOf(now.getEpochSecond());

		String signatureAlgorithm = "SHA256WithRSA";
		String providerName = provider.getName();

		ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm)
				.setProvider(providerName)
				.build(keyPair.getPrivate());

		JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
				dnName,
				serial,
				Date.from(now),
				Date.from(expiry),
				dnName,
				keyPair.getPublic()
		);

		BasicConstraints basicConstraints = new BasicConstraints(true);
		certBuilder.addExtension(
				new ASN1ObjectIdentifier("2.5.29.19"),
				true,
				basicConstraints
		);

		return new JcaX509CertificateConverter()
				.setProvider(providerName)
				.getCertificate(certBuilder.build(contentSigner));
	}
}