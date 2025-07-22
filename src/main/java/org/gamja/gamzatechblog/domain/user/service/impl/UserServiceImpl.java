package org.gamja.gamzatechblog.domain.user.service.impl;

import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.domain.comment.service.port.CommentRepository;
import org.gamja.gamzatechblog.domain.like.service.port.LikeRepository;
import org.gamja.gamzatechblog.domain.post.service.port.PostRepository;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.profileimage.model.mapper.ProfileImageMapper;
import org.gamja.gamzatechblog.domain.profileimage.service.port.ProfileImageRepository;
import org.gamja.gamzatechblog.domain.user.controller.response.UserActivityResponse;
import org.gamja.gamzatechblog.domain.user.controller.response.UserProfileResponse;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UpdateProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UserProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.model.mapper.UserMapper;
import org.gamja.gamzatechblog.domain.user.model.mapper.UserProfileMapper;
import org.gamja.gamzatechblog.domain.user.model.type.UserRole;
import org.gamja.gamzatechblog.domain.user.service.UserService;
import org.gamja.gamzatechblog.domain.user.service.port.UserRepository;
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
	private final LikeRepository likeRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final ProfileImageRepository profileImageRepository;
	private final ProfileImageMapper profileImageMapper;

	@Transactional
	public User registerWithProvider(OAuthUserInfo info) {
		userValidator.validateDuplicateProviderId(info.getGithubId());
		User user = userMapper.toEntity(info);
		User savedUser = userRepository.saveUser(user);
		ProfileImage profileImage = profileImageMapper.fromOAuthUserInfo(info, savedUser);
		profileImageRepository.saveProfileImage(profileImage);
		return savedUser;
	}

	public boolean existsByGithubId(String githubId) {
		return userRepository.existsByGithubId(githubId);
	}

	/*
	필터로 유효성검사를 하지만, 서비스코드에서 한번 더 실행합니다.
	 */
	@Transactional(readOnly = true)
	public UserProfileResponse getCurrentUserProfile(User currentUser) {
		User user = userValidator.validateAndGetUserByGithubId(currentUser.getGithubId());
		return userProfileMapper.toUserProfileResponse(user);
	}

	@Override
	@Transactional
	public UserProfileResponse setupUserProfile(String githubId, UserProfileRequest userProfileRequest) {
		userValidator.validateProfileRequest(userProfileRequest);
		User user = userValidator.validateAndGetUserByGithubId(githubId);
		userProfileMapper.completeProfile(userProfileRequest, user);
		user.setUserRole(UserRole.USER);
		User saved = userRepository.saveUser(user);
		return userProfileMapper.toUserProfileResponse(saved);
	}

	@Override
	public UserActivityResponse getActivitySummary(User user) {
		int likedPostCount = likeRepository.countByUser(user);
		int writtenPostCount = postRepository.countByUser(user);
		int writtenCommentCount = commentRepository.countByUser(user);

		return new UserActivityResponse(likedPostCount, writtenPostCount, writtenCommentCount);
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
		userRepository.deleteUser(user);
	}

	@Override
	public User getUserByGithubId(String githubId) {
		return userValidator.validateAndGetUserByGithubId(githubId);
	}
}
