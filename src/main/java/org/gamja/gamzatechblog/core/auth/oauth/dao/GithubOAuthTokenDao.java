package org.gamja.gamzatechblog.core.auth.oauth.dao;

import java.util.Optional;

public interface GithubOAuthTokenDao {
	void saveOrUpdateByGithubId(String githubId, String oauthAccessToken);

	Optional<String> findOauthAccessTokenByGithubId(String githubId);
}
