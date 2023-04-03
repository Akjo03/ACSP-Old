package com.akjostudios.acsp.backend.services.discord;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscordContentService {
	private static final String BASE_URL = "https://cdn.discordapp.com/";

	public String getAvatarUrl(String userId, String avatarHash) {
		return BASE_URL + "avatars/" + userId + "/" + avatarHash + ".png?size=256";
	}
}