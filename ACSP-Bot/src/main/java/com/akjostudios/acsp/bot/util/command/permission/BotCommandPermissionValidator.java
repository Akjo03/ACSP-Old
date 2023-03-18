package com.akjostudios.acsp.bot.util.command.permission;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BotCommandPermissionValidator {
	private final List<BotCommandChannelPermissions> channelPermissions;

	public BotCommandPermissionValidator(List<BotCommandChannelPermissions> channelPermissions) {
		this.channelPermissions = channelPermissions;
	}

	@SuppressWarnings("CodeBlock2Expr")
	public boolean isInvalid(GuildMessageChannelUnion channel, Member member) {
		AtomicBoolean isAllowed = new AtomicBoolean(false);

		channelPermissions.stream()
				.filter(channelPermission -> channelPermission.getChannel().getId() == channel.getIdLong())
				.findFirst()
				.ifPresent(channelPermission -> {
					isAllowed.set(channelPermission.getAllowedRoles().stream()
							.anyMatch(role -> member.getRoles().stream()
									.anyMatch(memberRole -> memberRole.getIdLong() == role.getId())
							));
				});

		return !isAllowed.get();
	}
}