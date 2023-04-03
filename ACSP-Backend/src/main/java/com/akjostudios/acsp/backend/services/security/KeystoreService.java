package com.akjostudios.acsp.backend.services.security;

import com.akjostudios.acsp.backend.config.KeystoreConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Service
@RequiredArgsConstructor
public class KeystoreService {
	private final KeystoreConfig keystoreConfig;

	public KeyStore getKeystore() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
		Path keystorePath = Path.of(keystoreConfig.getPath());
		KeyStore keyStore = KeyStore.getInstance("JKS");

		if (Files.exists(keystorePath)) {
			keyStore.load(Files.newInputStream(keystorePath), keystoreConfig.getPassword().toCharArray());
		} else {
			return null;
		}

		return keyStore;
	}

	public void setKey(KeyStore keyStore, String alias, Key key) throws KeyStoreException {
		X509Certificate certificate = (X509Certificate) keyStore.getCertificate(keystoreConfig.getAlias());
		X509Certificate[] certificateChain = {certificate};
		keyStore.setKeyEntry(alias, key, keystoreConfig.getPassword().toCharArray(), certificateChain);
	}

	public PrivateKey getKey(KeyStore keyStore, String alias) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
		return (PrivateKey) keyStore.getKey(alias, keystoreConfig.getPassword().toCharArray());
	}

	public void deleteKey(KeyStore keyStore, String alias) throws KeyStoreException {
		keyStore.deleteEntry(alias);
	}

	public void saveKeystore(KeyStore keyStore) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
		Path keystorePath = Path.of(keystoreConfig.getPath());
		keyStore.store(Files.newOutputStream(keystorePath), keystoreConfig.getPassword().toCharArray());
	}
}