package com.akjostudios.acsp.bot.constants;

import com.akjostudios.acsp.bot.util.constants.IdentifiableConstants;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum AcspDiscordChannelCategories implements IdentifiableConstants {
	WELCOME_CATEGORY(1075515133494841374L, 1073274747468644373L),
	BEGIN_CATEGORY(1075515133494841377L, 1073346445958201375L),
	ADMIN_CATEGORY(1075515133494841380L, 1073273754110668931L);

	private final long localId;
	private final long prodId;

	AcspDiscordChannelCategories(long localId, long prodId) {
		this.localId = localId;
		this.prodId = prodId;
	}

	public static @Nullable AcspDiscordChannelCategories getCategoryByName(String name) {
		return Arrays.stream(AcspDiscordChannelCategories.values())
				.filter(category -> category.name().equals(name))
				.findFirst()
				.orElse(null);
	}

	@Override
	public long getLocalId() { return localId; }

	@Override
	public long getProdId() { return prodId; }
}