package org.gamja.gamzatechblog.core.auth.oauth.service;

import org.gamja.gamzatechblog.core.auth.oauth.mapper.GithubUserMapper;
import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OAuthService {

	private final GithubUserMapper githubUserMapper;

	public OAuthUserInfo getUserInfoFromGithub(String code) {
		return githubUserMapper.toOAuthUserInfo(code);
	}
}
