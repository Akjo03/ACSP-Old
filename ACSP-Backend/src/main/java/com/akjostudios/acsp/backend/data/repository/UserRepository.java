package com.akjostudios.acsp.backend.data.repository;

import com.akjostudios.acsp.backend.data.model.AcspUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<AcspUser, String> {
	@Query("{ 'userId': '?0' }")
	AcspUser findByUserId(String userId);

	long count();
}