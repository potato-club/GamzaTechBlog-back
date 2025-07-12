package org.gamja.gamzatechblog.domain.repository.port;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public interface GitHubRepoRepository {
	Optional<GitHubRepo> findByUser(User user);

	GitHubRepo gitHubRepoSave(GitHubRepo gitHubRepo);

}
