package com.akjostudios.acsp.backend.db;

import com.akjostudios.acsp.backend.model.AcspRole;
import com.akjostudios.acsp.backend.model.AcspRoles;
import com.akjostudios.acsp.backend.repository.RoleRepository;
import com.akjostudios.acsp.backend.util.DbInitializer;
import io.github.akjo03.lib.logging.Logger;
import io.github.akjo03.lib.logging.LoggerManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RolesDbInitializer implements DbInitializer {
	private static final Logger LOGGER = LoggerManager.getLogger(RolesDbInitializer.class);

	private final RoleRepository roleRepository;

	@Override
	public void init() {
		List<AcspRole> roles = roleRepository.findAll();
		if (roles.isEmpty()) {
			roleRepository.saveAll(List.of(
					new AcspRole(AcspRoles.USER.getRole(), List.of(
							"ME_USER:READ",
							"ME_USER.SESSION.REFRESH"
					))
			));
			LOGGER.success("Initialized roles table");
		}
	}
}