package com.akjostudios.acsp.backend.data.repository;

import com.akjostudios.acsp.backend.data.model.AcspUser;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<AcspUser, String> {
	@Nullable AcspUser findByUserId(String userId);

	void deleteByUserId(String userId);

	long count();
}