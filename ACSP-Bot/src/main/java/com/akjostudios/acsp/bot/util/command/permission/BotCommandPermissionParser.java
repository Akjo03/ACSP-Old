package com.akjostudios.acsp.bot.util.command.permission;

import com.akjostudios.acsp.bot.config.bot.command.permission.BotConfigCommandPermission;
import com.akjostudios.acsp.bot.constants.AcspDiscordChannelCategories;
import com.akjostudios.acsp.bot.constants.AcspDiscordChannels;
import com.akjostudios.acsp.bot.constants.AcspDiscordRoles;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BotCommandPermissionParser {
	private static final Logger LOGGER = LoggerManager.getLogger(BotCommandPermissionParser.class);

	private final String commandName;
	private final List<BotConfigCommandPermission> permissionDefinitions;

	public BotCommandPermissionParser(String commandName, List<BotConfigCommandPermission> permissionDefinitions) {
		this.commandName = commandName;
		this.permissionDefinitions = permissionDefinitions;
	}

	public BotCommandPermissionValidator parse() {
		List<BotCommandChannelPermissions> channelPermissions = new ArrayList<>();

		Map<AcspDiscordRoles, EnumSet<AcspDiscordChannels>> roleChannelMap = new EnumMap<>(AcspDiscordRoles.class);

		for (BotConfigCommandPermission permissionDefinition : permissionDefinitions) {
			List<String> channelDefinitions = permissionDefinition.getChannels();
			List<String> roleDefinitions = permissionDefinition.getRoles();

			if (channelDefinitions == null || roleDefinitions == null) {
				continue;
			}

			EnumSet<AcspDiscordRoles> roles = getRolesFromDefinitions(roleDefinitions);

			for (AcspDiscordRoles role : roles) {
				boolean roleExists = roleChannelMap.containsKey(role);
				EnumSet<AcspDiscordChannels> channels = roleExists ? roleChannelMap.get(role) : EnumSet.noneOf(AcspDiscordChannels.class);

				roleChannelMap.put(role, getChannelsFromDefinitions(channelDefinitions, channels));
			}
		}

		Map<AcspDiscordChannels, EnumSet<AcspDiscordRoles>> channelRoleMap = reverseMapping(roleChannelMap);

		for (Map.Entry<AcspDiscordChannels, EnumSet<AcspDiscordRoles>> entry : channelRoleMap.entrySet()) {
			AcspDiscordChannels channel = entry.getKey();
			EnumSet<AcspDiscordRoles> roles = entry.getValue();

			channelPermissions.add(new BotCommandChannelPermissions(channel, roles));
		}

		return new BotCommandPermissionValidator(channelPermissions);
	}

	private @NotNull EnumSet<AcspDiscordRoles> getRolesFromDefinitions(@NotNull List<String> roleDefinitions) {
		EnumSet<AcspDiscordRoles> roles = EnumSet.noneOf(AcspDiscordRoles.class);
		for (String roleDefinition : roleDefinitions) {
			List<String> exclusionRoleDefinitions = new ArrayList<>();
			List<String> simpleRoleDefinitions = new ArrayList<>();

			if (roleDefinition.startsWith("-")) {
				exclusionRoleDefinitions.add(roleDefinition.substring(1));
			} else {
				simpleRoleDefinitions.add(roleDefinition);
			}

			for (String exclusionRoleDefinition : exclusionRoleDefinitions) {
				if (exclusionRoleDefinition.equals("*")) {
					roles.clear();
					continue;
				}
				String roleName = exclusionRoleDefinition.toUpperCase();
				AcspDiscordRoles role = AcspDiscordRoles.getRoleByName(roleName);
				if (role == null) {
					LOGGER.warn("While parsing command permissions for command \"" + commandName + "\", role was not found: " + roleName);
					continue;
				}
				roles.remove(role);
			}

			for (String simpleRoleDefinition : simpleRoleDefinitions) {
				if (simpleRoleDefinition.equals("*")) {
					roles.addAll(EnumSet.allOf(AcspDiscordRoles.class));
					continue;
				}
				String roleName = simpleRoleDefinition.toUpperCase();
				AcspDiscordRoles role = AcspDiscordRoles.getRoleByName(roleName);
				if (role == null) {
					LOGGER.warn("While parsing command permissions for command \"" + commandName + "\", role was not found: " + roleName);
					continue;
				}
				roles.add(role);
			}
		}
		return roles;
	}

	@Contract("_, _ -> param2")
	private @NotNull EnumSet<AcspDiscordChannels> getChannelsFromDefinitions(@NotNull List<String> channelDefinitions, @NotNull EnumSet<AcspDiscordChannels> channels) {
		List<String> categoryChannelDefinitions = new ArrayList<>();
		List<String> exclusionChannelDefinitions = new ArrayList<>();
		List<String> simpleChannelDefinitions = new ArrayList<>();

		for (String channelDefinition : channelDefinitions) {
			if (channelDefinition.startsWith(">")) {
				categoryChannelDefinitions.add(channelDefinition.substring(1));
			} else if (channelDefinition.startsWith("-")) {
				exclusionChannelDefinitions.add(channelDefinition.substring(1));
			} else {
				simpleChannelDefinitions.add(channelDefinition);
			}
		}

		for (String categoryDefinition : categoryChannelDefinitions) {
			if (categoryDefinition.equals("*")) {
				channels.addAll(EnumSet.allOf(AcspDiscordChannels.class));
				continue;
			}
			String categoryName = categoryDefinition.toUpperCase();
			AcspDiscordChannelCategories category = AcspDiscordChannelCategories.getCategoryByName(categoryName);
			if (category == null) {
				LOGGER.warn("While parsing command permissions for command \"" + commandName + "\", category was not found: " + categoryName);
				continue;
			}
			EnumSet<AcspDiscordChannels> categoryChannels = AcspDiscordChannels.getChannelsByCategory(category);
			channels.addAll(categoryChannels);
		}

		for (String exclusionDefinition : exclusionChannelDefinitions) {
			if (exclusionDefinition.equals("*")) {
				channels.clear();
				continue;
			}
			String channelName = exclusionDefinition.toUpperCase();
			AcspDiscordChannels channel = AcspDiscordChannels.getChannelByName(channelName);
			if (channel == null) {
				LOGGER.warn("While parsing command permissions for command \"" + commandName + "\", channel was not found: " + channelName);
				continue;
			}
			channels.remove(channel);
		}

		for (String simpleDefinition : simpleChannelDefinitions) {
			if (simpleDefinition.equals("*")) {
				channels.addAll(EnumSet.allOf(AcspDiscordChannels.class));
				continue;
			}
			String channelName = simpleDefinition.toUpperCase();
			AcspDiscordChannels channel = AcspDiscordChannels.getChannelByName(channelName);
			if (channel == null) {
				LOGGER.warn("While parsing command permissions for command \"" + commandName + "\", channel was not found: " + channelName);
				continue;
			}
			channels.add(channel);
		}

		return channels;
	}

	private @NotNull Map<AcspDiscordChannels, EnumSet<AcspDiscordRoles>> reverseMapping(@NotNull Map<AcspDiscordRoles, @NotNull EnumSet<AcspDiscordChannels>> roleChannelMap) {
		Map<AcspDiscordChannels, EnumSet<AcspDiscordRoles>> channelRoleMap = new EnumMap<>(AcspDiscordChannels.class);

		for (Map.Entry<AcspDiscordRoles, EnumSet<AcspDiscordChannels>> entry : roleChannelMap.entrySet()) {
			AcspDiscordRoles role = entry.getKey();
			EnumSet<AcspDiscordChannels> channels = entry.getValue();
			for (AcspDiscordChannels channel : channels) {
				boolean channelExists = channelRoleMap.containsKey(channel);
				EnumSet<AcspDiscordRoles> roles = channelExists ? channelRoleMap.get(channel) : EnumSet.noneOf(AcspDiscordRoles.class);
				roles.add(role);
				channelRoleMap.putIfAbsent(channel, roles);
			}
		}

		return channelRoleMap;
	}
}