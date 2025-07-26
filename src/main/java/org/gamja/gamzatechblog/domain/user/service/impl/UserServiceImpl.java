package org.gamja.gamzatechblog.domain.user.service.impl;

import org.gamja.gamzatechblog.common.port.s3.S3ImageStorage;
import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.gamja.gamzatechblog.domain.comment.service.port.CommentRepository;
import org.gamja.gamzatechblog.domain.like.service.port.LikeRepository;
import org.gamja.gamzatechblog.domain.post.service.port.PostRepository;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.profileimage.validator.ProfileImageValidator;
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
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final UserValidator userValidator;
	private final UserProfileMapper userProfileMapper;
	private final LikeRepository likeRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final S3ImageStorage s3ImageStorage;

	private final ProfileImageValidator profileImageValidator;

	@Override
	@Transactional
	public User registerWithProvider(OAuthUserInfo info) {
		userValidator.validateDuplicateProviderId(info.getGithubId());

		User user = userMapper.toEntity(info);

		if (info.getProfileImageUrl() != null && !info.getProfileImageUrl().isBlank()) {
			ProfileImage pi = ProfileImage.builder()
				.profileImageUrl(info.getProfileImageUrl())
				.build();
			user.changeProfileImage(pi);
		}

		return userRepository.saveUser(user);
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
		log.info(">> updateProfile 시작: githubId={}, req={}", currentUser.getGithubId(), req);

		User user = userValidator.validateAndGetUserByGithubId(currentUser.getGithubId());
		userProfileMapper.applyProfileUpdates(req, user);
		UserProfileResponse userProfileResponse = userProfileMapper.toUserProfileResponse(user);

		log.info("<< updateProfile 완료: userId={}, response={}", user.getId(), userProfileResponse);
		return userProfileResponse;
	}

	@Override
	@Transactional
	public void withdraw(User currentUser) {
		User user = userValidator.validateAndGetUserByGithubId(currentUser.getGithubId());

		unlinkAndDeleteProfileImage(user);

		userRepository.deleteUser(user);
	}

	@Override
	public User getUserByGithubId(String githubId) {
		return userValidator.validateAndGetUserByGithubId(githubId);
	}

	private void unlinkAndDeleteProfileImage(User user) {
		ProfileImage img = user.getProfileImage();
		if (img == null)
			return;

		profileImageValidator.validateForDelete(img);

		try {
			s3ImageStorage.deleteByUrl(img.getProfileImageUrl());
		} catch (BusinessException e) {
			log.warn("S3 삭제 실패(무시): {}", e.getMessage());
		}

		// 연관만 끊기면 orphanRemoval=true 가 DB 삭제 처리
		user.setProfileImage(null);
	}

}
