package com.akjostudios.acsp.backend.repository;

import com.akjostudios.acsp.backend.model.AcspUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<AcspUser, String> {
	@Query("{ 'userId': '?0' }")
	AcspUser findByUserId(String userId);

	long count();
}