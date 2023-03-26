package com.akjostudios.acsp.backend.dto.user;

import com.akjostudios.acsp.backend.model.AcspUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class UserDto {
	private AcspUser user;
}