package com.akjostudios.acsp.backend.data.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("roles")
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class AcspRole {
	@Id
	private String id;

	private String name;
	private List<String> permissions;

	public AcspRole(String name, List<String> permissions) {
		this.name = name;
		this.permissions = permissions;
	}
}