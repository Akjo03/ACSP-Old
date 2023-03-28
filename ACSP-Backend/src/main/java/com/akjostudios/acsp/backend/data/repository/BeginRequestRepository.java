package com.akjostudios.acsp.backend.data.repository;

import com.akjostudios.acsp.backend.data.model.BeginRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BeginRequestRepository extends MongoRepository<BeginRequest, String> {
	@Query("{ 'userId': '?0' }")
	BeginRequest findByUserId(String userId);

	@Query("{ 'code': '?0' }")
	BeginRequest findByCode(String code);

	long count();
}