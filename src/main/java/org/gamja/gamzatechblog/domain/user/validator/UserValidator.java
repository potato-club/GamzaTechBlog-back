package org.gamja.gamzatechblog.domain.user.validator;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.domain.user.exception.DuplicateValueException;
import org.gamja.gamzatechblog.domain.user.exception.UserAlreadyRegisteredException;
import org.gamja.gamzatechblog.domain.user.exception.UserNotFoundException;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.repository.UserRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserValidator {

	private final UserRepository userRepository;

	public void validateDuplicateProviderId(String GithubId) {
		if (userRepository.existsByGithubId(GithubId)) {
			throw new UserAlreadyRegisteredException(ErrorCode.USER_ALREADY_REGISTERED);
		}
	}

	public void validateDuplicateNickname(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new DuplicateValueException(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}
	}

	public void validateDuplicateEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new DuplicateValueException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}
	}

	public User validateAndGetUserByGithubId(String githubId) {
		return userRepository.findByGithubId(githubId)
			.orElseThrow(UserNotFoundException::new);
	}
}
