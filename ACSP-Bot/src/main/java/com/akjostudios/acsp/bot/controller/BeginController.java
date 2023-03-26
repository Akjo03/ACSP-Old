package com.akjostudios.acsp.bot.controller;

import com.akjostudios.acsp.bot.constants.AcspDiscordChannels;
import com.akjostudios.acsp.bot.util.controller.AcspBotController;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/begin")
public class BeginController extends AcspBotController {
	@DeleteMapping("")
	public void delete(String messageId) {
		TextChannel beginChannel = jdaInstance.getTextChannelById(AcspDiscordChannels.BEGIN_CHANNEL.getId());
		if (beginChannel == null) { return; }

		try {
			beginChannel.deleteMessageById(messageId).queue();
		} catch (Exception ignored) {}
	}
}