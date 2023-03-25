package com.akjostudios.acsp.backend.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class BeginLinkResponseDto {
	private String beginLink;
}