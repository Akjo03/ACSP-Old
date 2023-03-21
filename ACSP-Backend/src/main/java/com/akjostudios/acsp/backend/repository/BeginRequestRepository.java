package com.akjostudios.acsp.backend.repository;

import com.akjostudios.acsp.backend.model.BeginRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BeginRequestRepository extends MongoRepository<BeginRequest, String> {
	@Query("{ 'userId': '?0' }")
	BeginRequest findByUserId(String userId);

	long count();
}