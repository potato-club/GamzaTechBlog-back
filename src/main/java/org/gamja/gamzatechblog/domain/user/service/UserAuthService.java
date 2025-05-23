package org.gamja.gamzatechblog.domain.user.service;

import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.domain.user.model.dto.UpdateProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.UserProfileDto;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.model.mapper.UserMapper;
import org.gamja.gamzatechblog.domain.user.model.mapper.UserProfileMapper;
import org.gamja.gamzatechblog.domain.user.repository.UserRepository;
import org.gamja.gamzatechblog.domain.user.validator.UserValidator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserAuthService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final UserValidator userValidator;
	private final UserProfileMapper userProfileMapper;
	private final RedisTemplate<String, String> redisTemplate;
	private final JwtProvider jwtProvider;

	@Transactional
	public User registerWithProvider(OAuthUserInfo info) {
		userValidator.validateDuplicateProviderId(info.getGithubId());
		User user = userMapper.toEntity(info);
		return userRepository.save(user);
	}

	public boolean existsByGithubId(String githubId) {
		return userRepository.existsByGithubId(githubId);
	}

	@Transactional(readOnly = true)
	public UserProfileDto getMyProfile(User currentUser) {
		return userProfileMapper.toUserProfileDto(currentUser);
	}

	@Transactional
	public UserProfileDto updateProfile(User currentUser, UpdateProfileRequest req) {
		User user = userValidator.validateAndGetUserByGithubId(currentUser.getGithubId());
		userProfileMapper.applyProfileUpdates(req, user);
		return userProfileMapper.toUserProfileDto(user);
	}

	@Transactional
	public void withdraw(User currentUser) {
		userRepository.delete(currentUser);
	}
}
