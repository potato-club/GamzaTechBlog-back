package org.gamja.gamzatechblog.core.auth.oauth.service;

import lombok.RequiredArgsConstructor;
import org.gamja.gamzatechblog.core.auth.oauth.client.GithubApiClient;
import org.gamja.gamzatechblog.core.auth.oauth.mapper.GithubUserMapper;
import org.gamja.gamzatechblog.core.auth.oauth.model.GithubUser;
import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthProvider;
import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuthService {

    private final GithubUserMapper githubUserMapper;

    public OAuthUserInfo getUserInfoFromGithub(String code) {
        return githubUserMapper.toOAuthUserInfo(code);
    }

    public OAuthUserInfo getUserByProvider(Map<String, Object> data, String provider) {
        if (data == null || data.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!OAuthProvider.GITHUB.equalsIgnoreCase(provider)) {
            throw new BusinessException(ErrorCode.METHOD_NOT_ALLOWED);
        }

        if (!data.containsKey("id")) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        try {
            return new GithubUser(data);
        } catch (ClassCastException e) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }


}
