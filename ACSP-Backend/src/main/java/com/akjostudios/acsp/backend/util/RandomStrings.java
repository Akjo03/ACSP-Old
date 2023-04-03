package com.akjostudios.acsp.backend.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

@UtilityClass
public class RandomStrings {
	public static String generate(int randomStrLength) {
		char[] possibleCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();
		return RandomStringUtils.random(randomStrLength, 0, possibleCharacters.length-1, false, false, possibleCharacters, new SecureRandom());
	}
}