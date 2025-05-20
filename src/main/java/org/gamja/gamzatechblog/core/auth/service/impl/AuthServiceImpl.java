package org.gamja.gamzatechblog.core.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.gamja.gamzatechblog.core.auth.dto.TokenResponse;
import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.core.auth.oauth.service.OAuthService;
import org.gamja.gamzatechblog.core.auth.service.AuthService;
import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.gamja.gamzatechblog.domain.user.service.UserAuthService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final OAuthService oAuthService;
    private final UserAuthService userAuthService;
    private final JwtProvider jwtProvider;
    @Override
    public TokenResponse loginWithGithub(String code) {
        if (!StringUtils.hasText(code)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "GitHub code가 누락되었습니다.");
        }
        OAuthUserInfo info = oAuthService.getUserInfoFromGithub(code);

        if (!userAuthService.existsByProviderId(info.getProviderId())) {
            userAuthService.registerWithProvider(info);
        }

        String accessToken  = jwtProvider.createAccessToken(info.getProviderId());
        String refreshToken = jwtProvider.createRefreshToken(info.getProviderId());
        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    public TokenResponse reissueRefreshToken(String refreshToken) {
        String providerId = jwtProvider.extractGithubIdFromRefreshToken(refreshToken);
        String newAccess  = jwtProvider.createAccessToken(providerId);
        String newRefresh = jwtProvider.createRefreshToken(providerId);
        return new TokenResponse(newAccess, newRefresh);
    }

}
