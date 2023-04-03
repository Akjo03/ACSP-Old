package com.akjostudios.acsp.backend.services.security;

import io.github.akjo03.lib.path.ProjectDirectory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Date;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class SecurityService {
	private static final Random RANDOM = new SecureRandom();

	private final ProjectDirectory projectDirectory;

	public SecretKey getKeyFromPassword(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
		return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
	}

	public IvParameterSpec generateIv() {
		byte[] iv = new byte[16];
		RANDOM.nextBytes(iv);
		return new IvParameterSpec(iv);
	}

	public String generateSalt() {
		byte[] salt = new byte[16];
		RANDOM.nextBytes(salt);
		return Base64.getEncoder()
				.encodeToString(salt);
	}

	public String encrypt(String input, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException,
			NoSuchAlgorithmException,
			InvalidAlgorithmParameterException,
			InvalidKeyException,
			IllegalBlockSizeException,
			BadPaddingException
	{
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] plainText = cipher.doFinal(input.getBytes());
		return Base64.getEncoder()
				.encodeToString(plainText);
	}

	public String decrypt(String input, SecretKey key, IvParameterSpec iv)
			throws NoSuchPaddingException,
			NoSuchAlgorithmException,
			InvalidAlgorithmParameterException,
			InvalidKeyException,
			IllegalBlockSizeException,
			BadPaddingException
	{
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] plainText = cipher.doFinal(Base64.getDecoder()
				.decode(input));
		return new String(plainText);
	}

	public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		return keyPairGenerator.generateKeyPair();
	}

	public String generateToken(String sessionId, String userId, long seconds, RSAPrivateKey privateKey) {
		Instant now = Instant.now();
		Instant expiration = now.plusSeconds(seconds);
		HashMap<String, Object> claims = new HashMap<>();
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(userId)
				.setHeaderParam("kid", sessionId)
				.setHeaderParam("typ", "JWT")
				.setIssuer(userId)
				.setIssuedAt(Date.from(now))
				.setExpiration(Date.from(expiration))
				.signWith(privateKey, SignatureAlgorithm.PS256)
				.compact();
	}

	public Claims verifyToken(String token, PublicKey publicKey) {
		return Jwts.parserBuilder()
				.setSigningKey(publicKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public IvParameterSpec getIv(String iv) {
		return new IvParameterSpec(Base64.getDecoder()
				.decode(iv));
	}

	public PublicKey getPublicKey(String publicKey, SecretKey key, IvParameterSpec iv)
			throws NoSuchAlgorithmException,
			InvalidKeySpecException,
			InvalidAlgorithmParameterException,
			NoSuchPaddingException,
			IllegalBlockSizeException,
			BadPaddingException,
			InvalidKeyException {
		String decryptedKey = decrypt(publicKey, key, iv);

		X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder()
				.decode(decryptedKey));
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

	public PublicKey getPublicKey(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
		RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) privateKey;
		RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent());
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(rsaPublicKeySpec);
	}
}