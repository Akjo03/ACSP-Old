package com.akjostudios.acsp.backend.data.repository;

import com.akjostudios.acsp.backend.data.model.AcspLoginRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface LoginRequestRepository extends MongoRepository<AcspLoginRequest, String> {
	@Query("{ 'code': '?0' }")
	AcspLoginRequest findByCode(String code);

	long count();
}