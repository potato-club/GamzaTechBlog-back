package org.gamja.gamzatechblog.domain.user.service.port;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.model.type.UserRole;

public interface UserRepository {
	Optional<User> findByGithubId(String githubId);

	boolean existsByGithubId(String githubId);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	boolean existsByStudentNumber(String studentNumber);

	User saveUser(User user);

	void deleteUser(User user);

	List<User> findAllByRole(UserRole role);

	Optional<User> findById(Long id);
}
