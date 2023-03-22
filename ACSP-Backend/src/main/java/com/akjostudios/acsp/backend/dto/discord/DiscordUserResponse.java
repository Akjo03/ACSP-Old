package com.akjostudios.acsp.backend.dto.discord;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class DiscordUserResponse {
	@JsonSerialize
	private String id;

	@JsonSerialize
	private String username;

	@JsonSerialize
	private String discriminator;

	@JsonSerialize
	private String avatar;

	@JsonSerialize
	private boolean bot;

	@JsonSerialize
	private boolean system;

	@JsonSerialize
	private boolean mfaEnabled;

	@JsonSerialize
	private String banner;

	@JsonSerialize
	private int accentColor;

	@JsonSerialize
	private String locale;

	@JsonSerialize
	private boolean verified;

	@JsonSerialize
	private String email;

	@JsonSerialize
	private int flags;

	@JsonSerialize
	private int premiumType;

	@JsonSerialize
	private int publicFlags;

	@JsonCreator
	public DiscordUserResponse(
			@JsonProperty("id") String id,
			@JsonProperty("username") String username,
			@JsonProperty("discriminator") String discriminator,
			@JsonProperty("avatar") String avatar,
			@JsonProperty("bot") boolean bot,
			@JsonProperty("system") boolean system,
			@JsonProperty("mfa_enabled") boolean mfaEnabled,
			@JsonProperty("banner") String banner,
			@JsonProperty("accent_color") int accentColor,
			@JsonProperty("locale") String locale,
			@JsonProperty("verified") boolean verified,
			@JsonProperty("email") String email,
			@JsonProperty("flags") int flags,
			@JsonProperty("premium_type") int premiumType,
			@JsonProperty("public_flags") int publicFlags
	) {
		this.id = id;
		this.username = username;
		this.discriminator = discriminator;
		this.avatar = avatar;
		this.bot = bot;
		this.system = system;
		this.mfaEnabled = mfaEnabled;
		this.banner = banner;
		this.accentColor = accentColor;
		this.locale = locale;
		this.verified = verified;
		this.email = email;
		this.flags = flags;
		this.premiumType = premiumType;
		this.publicFlags = publicFlags;
	}
}