package org.gamja.gamzatechblog.domain.user.service.impl;

import org.gamja.gamzatechblog.common.port.s3.S3ImageStorage;
import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.gamja.gamzatechblog.domain.comment.service.port.CommentRepository;
import org.gamja.gamzatechblog.domain.like.service.port.LikeRepository;
import org.gamja.gamzatechblog.domain.post.service.port.PostRepository;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.profileimage.validator.ProfileImageValidator;
import org.gamja.gamzatechblog.domain.tag.service.port.TagRepository;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
	private final TagRepository tagRepository;

	private final ProfileImageValidator profileImageValidator;
	private static final String PROFILE_IMAGES_PREFIX = "profile-images";

	@Override
	@Transactional
	public User registerWithProvider(OAuthUserInfo info) {
		userValidator.validateDuplicateProviderId(info.getGithubId());
		User user = userMapper.toEntity(info);
		maybeAttachProfileImageFromGithub(user, info.getProfileImageUrl());
		return userRepository.saveUser(user);
	}

	@Transactional(readOnly = true)
	public boolean existsByGithubId(String githubId) {
		return userRepository.existsByGithubId(githubId);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "userProfile", key = "#p0.githubId", unless = "#result == null")
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
		user.setUserRole(UserRole.PENDING);
		User saved = userRepository.saveUser(user);
		return userProfileMapper.toUserProfileResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public UserActivityResponse getActivitySummary(User user) {
		int likedPostCount = likeRepository.countByUser(user);
		int writtenPostCount = postRepository.countByUser(user);
		int writtenCommentCount = commentRepository.countByUser(user);
		return new UserActivityResponse(likedPostCount, writtenPostCount, writtenCommentCount);
	}

	@Transactional
	@CacheEvict(value = "userProfile", key = "#p0.githubId")
	public UserProfileResponse updateProfile(User currentUser, UpdateProfileRequest req) {
		if (log.isDebugEnabled()) {
			log.debug("updateProfile start githubId={}", currentUser.getGithubId());
		}
		User user = userValidator.validateAndGetUserByGithubId(currentUser.getGithubId());
		userProfileMapper.applyProfileUpdates(req, user);
		UserProfileResponse resp = userProfileMapper.toUserProfileResponse(user);
		if (log.isDebugEnabled()) {
			log.debug("updateProfile done userId={}", user.getId());
		}
		return resp;
	}

	@Override
	@Transactional
	public void withdraw(User currentUser) {
		User user = userValidator.validateAndGetUserByGithubId(currentUser.getGithubId());

		deleteAllPostImagesSafely(user);
		unlinkAndDeleteProfileImageSafely(user);

		userRepository.deleteUser(user);
		tagRepository.deleteOrphanTags();
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserByGithubId(String githubId) {
		return userValidator.validateAndGetUserByGithubId(githubId);
	}

	private void maybeAttachProfileImageFromGithub(User user, String githubImageUrl) {
		if (!StringUtils.hasText(githubImageUrl))
			return;
		profileImageValidator.validateUrl(githubImageUrl);
		try {
			String s3Url = s3ImageStorage.uploadFromUrl(githubImageUrl, PROFILE_IMAGES_PREFIX);
			user.changeProfileImage(ProfileImage.builder().profileImageUrl(s3Url).build());
		} catch (BusinessException ex) {
			log.warn("프로필 이미지 업로드 실패(계속 진행): {}", ex.getMessage());
		}
	}

	private void deleteAllPostImagesSafely(User user) {
		if (user.getPosts() == null)
			return;
		user.getPosts().forEach(post -> {
			if (post.getImages() == null)
				return;
			post.getImages().forEach(img -> {
				try {
					s3ImageStorage.deleteByUrl(img.getPostImageUrl());
				} catch (BusinessException e) {
					log.warn("포스트 이미지 S3 삭제 실패(무시): {}", e.getMessage());
				}
			});
		});
	}

	private void unlinkAndDeleteProfileImageSafely(User user) {
		ProfileImage img = user.getProfileImage();
		if (img == null)
			return;
		profileImageValidator.validateForDelete(img);
		try {
			s3ImageStorage.deleteByUrl(img.getProfileImageUrl());
		} catch (BusinessException e) {
			log.warn("S3 삭제 실패(무시): {}", e.getMessage());
		}
		user.setProfileImage(null);
	}

}
