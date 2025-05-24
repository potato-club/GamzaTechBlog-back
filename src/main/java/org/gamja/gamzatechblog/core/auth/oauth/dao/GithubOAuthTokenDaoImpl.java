package org.gamja.gamzatechblog.core.auth.oauth.dao;

import java.util.Optional;

import org.gamja.gamzatechblog.core.auth.oauth.model.entity.GithubOauthToken;
import org.gamja.gamzatechblog.core.auth.oauth.repository.GithubOAuthTokenRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.validator.UserValidator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GithubOAuthTokenDaoImpl implements GithubOAuthTokenDao {
	private final GithubOAuthTokenRepository repo;
	private final UserValidator userValidator;

	@Override
	@Transactional
	public void saveOrUpdateByGithubId(String githubId, String oauthAccessToken) {
		User user = userValidator.validateAndGetUserByGithubId(githubId);
		GithubOauthToken tokenEntity = repo.findByUser(user)
			.map(e -> {
				e.setOauthAccessToken(oauthAccessToken);
				return e;
			})
			.orElse(GithubOauthToken.builder()
				.user(user)
				.oauthAccessToken(oauthAccessToken)
				.build());

		repo.save(tokenEntity);
	}

	@Override
	public Optional<String> findOauthAccessTokenByGithubId(String githubId) {
		User user = userValidator.validateAndGetUserByGithubId(githubId);
		return repo.findByUser(user)
			.map(GithubOauthToken::getOauthAccessToken);
	}
}
