package org.gamja.gamzatechblog.domain.user.service;

import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.model.mapper.UserMapper;
import org.gamja.gamzatechblog.domain.user.repository.UserRepository;
import org.gamja.gamzatechblog.domain.user.validator.UserValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserAuthService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final UserValidator userValidator;

	@Transactional
	public User registerWithProvider(OAuthUserInfo info) {
		userValidator.validateDuplicateProviderId(info.getGithubId());
		User user = userMapper.toEntity(info);
		return userRepository.save(user);
	}

	public boolean existsByGithubId(String githubId) {
		return userRepository.existsByGithubId(githubId);
	}
}
