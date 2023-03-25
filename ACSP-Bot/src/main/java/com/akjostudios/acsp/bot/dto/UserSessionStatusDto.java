package com.akjostudios.acsp.bot.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class UserSessionStatusDto {
	private String userId;
	private String sessionId;
	private String sessionStatus;
}