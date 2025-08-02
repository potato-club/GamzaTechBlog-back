package org.gamja.gamzatechblog.domain.user.infrastructure;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.model.type.UserRole;
import org.gamja.gamzatechblog.domain.user.service.port.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

	private final UserJpaRepository userJpaRepository;

	@Override
	public Optional<User> findByGithubId(String githubId) {
		return userJpaRepository.findByGithubId(githubId);
	}

	@Override
	public boolean existsByGithubId(String githubId) {
		return userJpaRepository.existsByGithubId(githubId);
	}

	@Override
	public boolean existsByEmail(String email) {
		return userJpaRepository.existsByEmail(email);
	}

	@Override
	public boolean existsByNickname(String nickname) {
		return userJpaRepository.existsByNickname(nickname);
	}

	@Override
	public boolean existsByStudentNumber(String studentNumber) {
		return userJpaRepository.existsByStudentNumber(studentNumber);
	}

	@Override
	public User saveUser(User user) {
		return userJpaRepository.save(user);
	}

	@Override
	public void deleteUser(User user) {
		userJpaRepository.delete(user);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findById(Long id) {
		return userJpaRepository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> findAllByRole(UserRole role) {
		return userJpaRepository.findAllByRole(role);
	}
}