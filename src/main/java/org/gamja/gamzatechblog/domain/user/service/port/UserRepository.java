package org.gamja.gamzatechblog.domain.user.service.port;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.user.model.entity.User;

public interface UserRepository {
	Optional<User> findByGithubId(String githubId);

	boolean existsByGithubId(String githubId);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	boolean existsByStudentNumber(String studentNumber);

	User save(User user);

	void delete(User user);
}
