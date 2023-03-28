package com.akjostudios.acsp.backend.data.dto.user;

import com.akjostudios.acsp.backend.data.model.AcspUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class UserDto {
	private AcspUser user;
}