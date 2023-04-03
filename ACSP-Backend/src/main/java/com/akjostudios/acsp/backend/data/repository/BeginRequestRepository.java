package com.akjostudios.acsp.backend.data.repository;

import com.akjostudios.acsp.backend.data.model.AcspBeginRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BeginRequestRepository extends MongoRepository<AcspBeginRequest, String> {
	@Query("{ 'userId': '?0' }")
	AcspBeginRequest findByUserId(String userId);

	@Query("{ 'code': '?0' }")
	AcspBeginRequest findByCode(String code);

	long count();
}