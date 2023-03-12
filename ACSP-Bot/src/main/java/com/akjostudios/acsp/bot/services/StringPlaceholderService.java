package com.akjostudios.acsp.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StringPlaceholderService {
	public String replacePlaceholders(String str, String[] placeholders) {
		for (int i = 0; i < placeholders.length; i++) {
			if (str == null) {
				return null;
			}
			str = str.replace("$" + i, placeholders[i]);
		}
		return str;
	}
}