package com.akjostudios.acsp.backend.repository;

import com.akjostudios.acsp.backend.model.AcspUserSession;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserSessionRepository extends MongoRepository<AcspUserSession, String> {
	@Query("{ 'userId' : ?0 }")
	@Nullable AcspUserSession findByUserId(String userId);

	@Query("{ 'status': ?0 }")
	@Nullable AcspUserSession findByStatus(String status);

	@Query("{ 'sessionId': ?0 }")
	@Nullable AcspUserSession findBySessionId(String sessionId);
}