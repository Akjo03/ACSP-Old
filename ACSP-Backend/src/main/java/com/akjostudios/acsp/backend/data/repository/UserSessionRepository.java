package com.akjostudios.acsp.backend.data.repository;

import com.akjostudios.acsp.backend.data.model.AcspUserSession;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserSessionRepository extends MongoRepository<AcspUserSession, String> {
	@Nullable AcspUserSession findByUserId(String userId);

	@Nullable AcspUserSession findBySessionId(String sessionId);

	void deleteBySessionId(String sessionId);

	long count();
}