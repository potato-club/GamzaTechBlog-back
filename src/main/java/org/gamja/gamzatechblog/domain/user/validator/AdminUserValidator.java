package org.gamja.gamzatechblog.domain.user.validator;

import org.gamja.gamzatechblog.domain.user.exception.UserNotFoundException;
import org.gamja.gamzatechblog.domain.user.exception.UserNotPendingException;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.model.type.UserRole;
import org.gamja.gamzatechblog.domain.user.service.port.UserRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

//변경 예정
@Component
@RequiredArgsConstructor
public class AdminUserValidator {

	private final UserRepository userRepository;

	public User validateAndGetUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(UserNotFoundException::new);
	}

	public void validatePending(User user) {
		if (user.getRole() != UserRole.PENDING) {
			throw new UserNotPendingException();
		}
	}
}
