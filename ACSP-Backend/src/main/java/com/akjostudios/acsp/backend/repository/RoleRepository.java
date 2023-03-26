package com.akjostudios.acsp.backend.repository;

import com.akjostudios.acsp.backend.model.AcspRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RoleRepository extends MongoRepository<AcspRole, String> {
	@Query("{ 'name': '?0' }")
	AcspRole findByName(String name);

	long count();
}