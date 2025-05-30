package org.gamja.gamzatechblog.domain.user.service.impl;

import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UpdateProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UserProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.response.UserProfileResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.model.mapper.UserMapper;
import org.gamja.gamzatechblog.domain.user.model.mapper.UserProfileMapper;
import org.gamja.gamzatechblog.domain.user.model.type.UserRole;
import org.gamja.gamzatechblog.domain.user.repository.UserRepository;
import org.gamja.gamzatechblog.domain.user.service.UserService;
import org.gamja.gamzatechblog.domain.user.validator.UserValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final UserValidator userValidator;
	private final UserProfileMapper userProfileMapper;

	@Transactional
	public User registerWithProvider(OAuthUserInfo info) {
		userValidator.validateDuplicateProviderId(info.getGithubId());
		User user = userMapper.toEntity(info);
		return userRepository.save(user);
	}

	public boolean existsByGithubId(String githubId) {
		return userRepository.existsByGithubId(githubId);
	}

	/*
	필터로 유효성검사를 하지만, 서비스코드에서 한번 더 실행합니다.
	 */
	@Transactional(readOnly = true)
	public UserProfileResponse getMyProfile(User currentUser) {
		User user = userValidator.validateAndGetUserByGithubId(currentUser.getGithubId());
		return userProfileMapper.toUserProfileResponse(user);
	}

	@Override
	@Transactional
	public UserProfileResponse completeProfile(String githubId, UserProfileRequest userProfileRequest) {
		userValidator.validateProfileRequest(userProfileRequest);
		User user = userValidator.validateAndGetUserByGithubId(githubId);
		userProfileMapper.completeProfile(userProfileRequest, user);
		user.setUserRole(UserRole.USER);
		User saved = userRepository.save(user);
		return userProfileMapper.toUserProfileResponse(saved);
	}

	@Transactional
	public UserProfileResponse updateProfile(User currentUser, UpdateProfileRequest req) {
		User user = userValidator.validateAndGetUserByGithubId(currentUser.getGithubId());
		userProfileMapper.applyProfileUpdates(req, user);
		return userProfileMapper.toUserProfileResponse(user);
	}

	@Transactional
	public void withdraw(User currentUser) {
		User user = userValidator.validateAndGetUserByGithubId(currentUser.getGithubId());
		userRepository.delete(user);
	}

	@Override
	public User findByGithubId(String githubId) {
		return userValidator.validateAndGetUserByGithubId(githubId);
	}
}
