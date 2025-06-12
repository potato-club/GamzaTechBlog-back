package org.gamja.gamzatechblog.core.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccessTokenResponse {
	private final String authorization;
}

