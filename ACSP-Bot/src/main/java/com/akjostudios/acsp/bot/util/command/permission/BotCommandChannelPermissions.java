package com.akjostudios.acsp.bot.util.command.permission;

import com.akjostudios.acsp.bot.constants.AcspDiscordChannels;
import com.akjostudios.acsp.bot.constants.AcspDiscordRoles;
import lombok.Getter;

import java.util.EnumSet;

@Getter
@SuppressWarnings("ClassCanBeRecord")
public class BotCommandChannelPermissions {
	private final AcspDiscordChannels channel;
	private final EnumSet<AcspDiscordRoles> allowedRoles;

	public BotCommandChannelPermissions(AcspDiscordChannels channel, EnumSet<AcspDiscordRoles> allowedRoles) {
		this.channel = channel;
		this.allowedRoles = allowedRoles;
	}
}