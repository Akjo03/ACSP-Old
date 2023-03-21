package com.akjostudios.acsp.bot.util.command.permission;

import com.akjostudios.acsp.bot.constants.AcspDiscordRoles;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BotCommandPermissionValidator {
	private final List<BotCommandChannelPermissions> channelPermissions;

	public BotCommandPermissionValidator(List<BotCommandChannelPermissions> channelPermissions) {
		this.channelPermissions = channelPermissions;
	}

	public BotCommandPermissionValidation getValidation(GuildMessageChannelUnion channel, Member member) {
		AtomicBoolean isAllowed = new AtomicBoolean(false);

		if (channelPermissions.stream().map(BotCommandChannelPermissions::getChannel).noneMatch(channelP -> channelP.getId() == channel.getIdLong())) {
			return BotCommandPermissionValidation.denied("errors.command_missing_permissions.reason.channel");
		}

		for (BotCommandChannelPermissions permissionDefinition : channelPermissions) {
			if (permissionDefinition.getAllowedRoles().contains(AcspDiscordRoles.EVERYONE_ROLE)) {
				isAllowed.set(true);
				break;
			}

			for (AcspDiscordRoles role : permissionDefinition.getAllowedRoles()) {
				List<Role> memberRoles = member.getRoles();
				for (Role memberRole : memberRoles) {
					if (memberRole.getIdLong() == role.getId()) {
						isAllowed.set(true);
						break;
					}
				}
			}
		}

		return isAllowed.get()
				? BotCommandPermissionValidation.allowed()
				: BotCommandPermissionValidation.denied("errors.command_missing_permissions.reason.role");
	}
}