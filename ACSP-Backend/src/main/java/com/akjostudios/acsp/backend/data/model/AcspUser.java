package com.akjostudios.acsp.backend.data.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class AcspUser {
	@Id
	private String id;

	private String userId;
	private String email;
	private String role;
}