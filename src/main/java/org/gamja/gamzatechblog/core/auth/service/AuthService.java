package org.gamja.gamzatechblog.core.auth.service;

import org.gamja.gamzatechblog.core.auth.dto.TokenResponse;

public interface AuthService {
	TokenResponse loginWithGithub(String code);

	TokenResponse reissueRefreshToken(String refreshToken);

}
