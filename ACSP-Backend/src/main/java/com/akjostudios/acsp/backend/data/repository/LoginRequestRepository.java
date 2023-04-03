package com.akjostudios.acsp.backend.data.repository;

import com.akjostudios.acsp.backend.data.model.AcspLoginRequest;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LoginRequestRepository extends MongoRepository<AcspLoginRequest, String> {
	@Nullable AcspLoginRequest findByCode(String code);

	long count();
}