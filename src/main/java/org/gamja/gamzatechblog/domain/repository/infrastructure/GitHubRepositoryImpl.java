package org.gamja.gamzatechblog.domain.repository.infrastructure;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.gamja.gamzatechblog.domain.repository.port.GitHubRepoRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GitHubRepositoryImpl implements GitHubRepoRepository {
	private final GitHubRepoJpaRepository githubRepoJpaRepository;

	@Override
	public Optional<GitHubRepo> findByUser(User user) {
		return githubRepoJpaRepository.findByUser(user);
	}

	@Override
	public GitHubRepo gitHubRepoSave(GitHubRepo gitHubRepo) {
		return githubRepoJpaRepository.save(gitHubRepo);
	}
}
