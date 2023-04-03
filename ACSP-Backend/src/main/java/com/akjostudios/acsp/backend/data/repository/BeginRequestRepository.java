package com.akjostudios.acsp.backend.data.repository;

import com.akjostudios.acsp.backend.data.model.AcspBeginRequest;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BeginRequestRepository extends MongoRepository<AcspBeginRequest, String> {
	@Nullable AcspBeginRequest findByUserId(String userId);

	@Nullable AcspBeginRequest findByCode(String code);

	long count();
}