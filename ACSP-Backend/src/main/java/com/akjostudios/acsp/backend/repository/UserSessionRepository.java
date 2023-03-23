package com.akjostudios.acsp.backend.repository;

import com.akjostudios.acsp.backend.model.AcspUserSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserSessionRepository extends MongoRepository<AcspUserSession, String> {
	@Query("{ 'userId' : ?0 }")
	AcspUserSession findByUserId(String userId);

	@Query("{ 'status': ?0 }")
	AcspUserSession findByStatus(String status);

	@Query("{ 'sessionId': ?0 }")
	AcspUserSession findBySessionId(String sessionId);
}