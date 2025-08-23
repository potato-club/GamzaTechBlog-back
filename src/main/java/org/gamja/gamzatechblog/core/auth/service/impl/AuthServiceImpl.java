package org.gamja.gamzatechblog.core.auth.service.impl;

import java.time.Duration;

import org.gamja.gamzatechblog.core.auth.dto.TokenResponse;
import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.auth.oauth.dao.RefreshTokenDao;
import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.core.auth.oauth.service.OAuthService;
import org.gamja.gamzatechblog.core.auth.service.AuthService;
import org.gamja.gamzatechblog.core.auth.service.BlacklistService;
import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.gamja.gamzatechblog.core.error.exception.OAuthException;
import org.gamja.gamzatechblog.domain.user.service.impl.UserServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(30);

	private final OAuthService oAuthService;
	private final UserServiceImpl userAuthService;
	private final JwtProvider jwtProvider;
	private final RefreshTokenDao refreshTokenDao;
	private final BlacklistService blacklistService;

	@Override
	public TokenResponse loginWithGithub(String code) {
		if (!StringUtils.hasText(code)) {
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "GitHub code가 누락되었습니다.");
		}
		OAuthUserInfo info = oAuthService.getUserInfoFromGithub(code);
		if (!userAuthService.existsByGithubId(info.getGithubId())) {
			userAuthService.registerWithProvider(info);
		}
		return issueTokens(info.getGithubId());
	}

	@Override
	public TokenResponse reissueRefreshToken(String oldRefreshToken) {
		String userId = refreshTokenDao.findUserIdByRefreshToken(oldRefreshToken)
			.orElseThrow(() -> new OAuthException(ErrorCode.JWT_NOT_FOUND));

		String accessToken = jwtProvider.createAccessToken(userId);
		refreshTokenDao.touchTtl(oldRefreshToken, REFRESH_TOKEN_TTL);

		return new TokenResponse(accessToken, oldRefreshToken);
	}

	@Override
	public void logout(String githubId) {
		blacklistService.blacklistTokens(githubId);
	}

	private TokenResponse issueTokens(String userId) {
		String accessToken = jwtProvider.createAccessToken(userId);
		String refreshToken = jwtProvider.createRefreshToken(userId);
		refreshTokenDao.rotateRefreshToken(userId, refreshToken, REFRESH_TOKEN_TTL);
		return new TokenResponse(accessToken, refreshToken);
	}
}
