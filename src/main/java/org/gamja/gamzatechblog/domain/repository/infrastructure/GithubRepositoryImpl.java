package org.gamja.gamzatechblog.domain.repository.infrastructure;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.gamja.gamzatechblog.domain.repository.port.GitHubRepoRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GithubRepositoryImpl implements GitHubRepoRepository {
	private final GithubJpaRepository githubJpaRepository;

	@Override
	public Optional<GitHubRepo> findByUser(User user) {
		return githubJpaRepository.findByUser(user);
	}
}
