package com.akjostudios.acsp.backend.data.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("beginrequests")
@RequiredArgsConstructor
@Getter
@Setter
public class AcspBeginRequest {
	@Id
	private String id;

	private String userId;
	private String code;
	private String salt;
}