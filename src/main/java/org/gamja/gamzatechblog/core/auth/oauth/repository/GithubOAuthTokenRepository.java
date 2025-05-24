package org.gamja.gamzatechblog.core.auth.oauth.repository;

import java.util.Optional;

import org.gamja.gamzatechblog.core.auth.oauth.model.entity.GithubOauthToken;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubOAuthTokenRepository extends JpaRepository<GithubOauthToken, Long> {

	Optional<GithubOauthToken> findByUser(User user);
}
