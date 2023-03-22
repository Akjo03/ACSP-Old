package com.akjostudios.acsp.backend.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("beginrequests")
@RequiredArgsConstructor
@Getter
@Setter
public class BeginRequest {
	@Id
	private String id;

	private String userId;
	private String code;
	private String authState;
	private String linkMessageId;
}