package org.gamja.gamzatechblog.domain.user.validator;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.domain.user.exception.UserAlreadyRegisteredException;
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
}
