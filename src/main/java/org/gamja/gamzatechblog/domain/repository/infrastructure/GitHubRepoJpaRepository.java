package org.gamja.gamzatechblog.domain.repository.infrastructure;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GitHubRepoJpaRepository extends JpaRepository<GitHubRepo, Long> {
	Optional<GitHubRepo> findByUser(User user);
}
