package org.gamja.gamzatechblog.core.auth.service.impl;

import org.gamja.gamzatechblog.core.auth.dto.TokenResponse;
import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.auth.oauth.dao.RefreshTokenDao;
import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.core.auth.oauth.service.OAuthService;
import org.gamja.gamzatechblog.core.auth.service.AuthService;
import org.gamja.gamzatechblog.core.auth.service.BlacklistService;
import org.gamja.gamzatechblog.core.config.security.GithubLoginProperties;
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

	private final OAuthService oAuthService;
	private final UserServiceImpl userAuthService;
	private final JwtProvider jwtProvider;
	private final RefreshTokenDao refreshTokenDao;
	private final BlacklistService blacklistService;
	private final GithubLoginProperties githubLoginProperties;

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
		refreshTokenDao.touchTtl(oldRefreshToken, githubLoginProperties.getRefreshTokenTtl());

		return new TokenResponse(accessToken, oldRefreshToken);
	}

	@Override
	public void logout(String githubId) {
		blacklistService.blacklistTokens(githubId);
	}

	private TokenResponse issueTokens(String userId) {
		String accessToken = jwtProvider.createAccessToken(userId);
		String refreshToken = jwtProvider.createRefreshToken(userId);
		refreshTokenDao.rotateRefreshToken(userId, refreshToken, githubLoginProperties.getRefreshTokenTtl());
		return new TokenResponse(accessToken, refreshToken);
	}
}
