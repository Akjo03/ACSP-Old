package com.akjostudios.acsp.bot.constants;

import com.akjostudios.acsp.bot.util.constants.IdentifiableConstants;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public enum AcspDiscordRoles implements IdentifiableConstants {
	EVERYONE_ROLE(1075515131288625182L, 1073266273619816549L),
	BOTS_ROLE(1075515131288625183L, 1073678363237027915L),
	ADMIN_ROLE(1075515131288625184L, 1073273895395791009L);

	private final long localId;
	private final long prodId;

	AcspDiscordRoles(long localId, long prodId) {
		this.localId = localId;
		this.prodId = prodId;
	}

	public static @Nullable AcspDiscordRoles getCategoryByName(String name) {
		return Arrays.stream(AcspDiscordRoles.values())
				.filter(category -> category.name().equals(name))
				.findFirst()
				.orElse(null);
	}

	@Override
	public long getLocalId() { return localId; }

	@Override
	public long getProdId() { return prodId; }
}