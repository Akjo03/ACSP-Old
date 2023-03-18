package com.akjostudios.acsp.bot.constants;

import com.akjostudios.acsp.bot.util.constants.IdentifiableConstants;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public enum AcspDiscordChannels implements IdentifiableConstants {
	WELCOME_CHANNEL(1075515133494841375L, 1073275189338591233L, AcspDiscordChannelCategories.WELCOME_CATEGORY),
	RULES_CHANNEL(1075515133494841376L, 1073273398693724332L, AcspDiscordChannelCategories.WELCOME_CATEGORY),
	BEGIN_CHANNEL(1075515133494841378L, 1073346501314629732L, AcspDiscordChannelCategories.BEGIN_CATEGORY),
	ADMIN_CHANNEL(1075515133494841381L, 1073273398693724333L, AcspDiscordChannelCategories.ADMIN_CATEGORY);

	private final long localId;
	private final long prodId;
	@Getter
	private final AcspDiscordChannelCategories category;

	AcspDiscordChannels(long localId, long prodId, AcspDiscordChannelCategories category) {
		this.localId = localId;
		this.prodId = prodId;
		this.category = category;
	}

	public static @Nullable AcspDiscordChannels getChannelByName(String name) {
		return Arrays.stream(AcspDiscordChannels.values())
				.filter(category -> category.name().equals(name))
				.findFirst()
				.orElse(null);
	}

	public static EnumSet<AcspDiscordChannels> getChannelsByCategory(AcspDiscordChannelCategories category) {
		return Arrays.stream(AcspDiscordChannels.values())
				.filter(channel -> channel.getCategory().equals(category))
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(AcspDiscordChannels.class)));
	}

	@Override
	public long getLocalId() { return localId; }

	@Override
	public long getProdId() { return prodId; }
}