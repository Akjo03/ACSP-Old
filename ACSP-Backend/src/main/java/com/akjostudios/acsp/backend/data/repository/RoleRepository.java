package com.akjostudios.acsp.backend.data.repository;

import com.akjostudios.acsp.backend.data.model.AcspRole;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<AcspRole, String> {
	@Nullable AcspRole findByName(String name);

	long count();
}