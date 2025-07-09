package org.gamja.gamzatechblog.domain.user.service.port;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.user.model.entity.User;

public class UserFakeUserRepository implements UserRepository {
	private final Map<String, User> userHashMap = new HashMap<>();

	@Override
	public Optional<User> findByGithubId(String githubId) {
		return Optional.ofNullable(userHashMap.get(githubId));
	}

	@Override
	public boolean existsByGithubId(String githubId) {
		return userHashMap.containsKey(githubId);
	}

	@Override
	public boolean existsByEmail(String email) {
		return userHashMap.values().stream()
			.anyMatch(u -> u.getEmail().equals(email));
	}

	@Override
	public boolean existsByNickname(String nickname) {
		return userHashMap.values().stream()
			.anyMatch(u -> u.getNickname().equals(nickname));
	}

	@Override
	public boolean existsByStudentNumber(String studentNumber) {
		return userHashMap.values().stream()
			.anyMatch(u -> u.getStudentNumber().equals(studentNumber));
	}

	@Override
	public User saveUser(User user) {
		userHashMap.put(user.getGithubId(), user);
		return user;
	}

	@Override
	public void deleteUser(User user) {
		userHashMap.remove(user.getGithubId());
	}
}
