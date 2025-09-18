package org.gamja.gamzatechblog.domain.user.infrastructure;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.model.type.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
	Optional<User> findByGithubId(String githubId);

	boolean existsByGithubId(String githubId);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	boolean existsByStudentNumber(String studentNumber);

	List<User> findAllByRole(UserRole role);

	Optional<User> findByNickname(String nickname);
}
