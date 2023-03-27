package com.akjostudios.acsp.backend.db;

import com.akjostudios.acsp.backend.model.AcspRole;
import com.akjostudios.acsp.backend.model.AcspRoles;
import com.akjostudios.acsp.backend.repository.RoleRepository;
import com.akjostudios.acsp.backend.util.DbInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RolesDbInitializer implements DbInitializer {
	private final RoleRepository roleRepository;

	@Override
	public void init() {
		List<AcspRole> roles = roleRepository.findAll();
		if (roles.isEmpty()) {
			roleRepository.save(new AcspRole(AcspRoles.USER.getRole(), List.of(
					"ME_USER:READ",
					"ME_USER.SESSION.REFRESH"
			)));
		}
	}
}