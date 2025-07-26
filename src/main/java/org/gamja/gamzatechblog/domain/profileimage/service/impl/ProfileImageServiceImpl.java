package org.gamja.gamzatechblog.domain.profileimage.service.impl;

import org.gamja.gamzatechblog.common.port.s3.S3ImageStorage;
import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.profileimage.model.mapper.ProfileImageMapper;
import org.gamja.gamzatechblog.domain.profileimage.service.ProfileImageService;
import org.gamja.gamzatechblog.domain.profileimage.service.port.ProfileImageRepository;
import org.gamja.gamzatechblog.domain.profileimage.validator.ProfileImageValidator;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.service.port.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 리팩토링 예정
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProfileImageServiceImpl implements ProfileImageService {

	private final S3ImageStorage s3ImageStorage;
	private final ProfileImageValidator validator;
	private final ProfileImageRepository profileImageRepository;
	private final @Qualifier("profileImageMapperImpl") ProfileImageMapper mapper;
	private final UserRepository userRepository;

	// @Override
	// @Transactional
	// public ProfileImage uploadProfileImage(MultipartFile file, User user) {
	// 	validator.validateFile(file);
	// 	user.setProfileImage(null);
	// 	profileImageRepository.deleteByUser(user);
	// 	profileImageRepository.flush();
	// 	String url = s3ImageStorage.uploadFile(file);
	// 	ProfileImage newImg = ProfileImage.builder()
	// 		.user(user)
	// 		.profileImageUrl(url)
	// 		.build();
	// 	ProfileImage saved = profileImageRepository.saveProfileImage(newImg);
	// 	user.setProfileImage(saved);
	//
	// 	return saved;
	// }

	@Override
	@Transactional
	public ProfileImage uploadProfileImage(MultipartFile file, User currentUser) {
		User user = userRepository.findByGithubId(currentUser.getGithubId())
			.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "사용자를 찾을 수 없습니다."));

		validator.validateFile(file);
		unlinkAndDeleteProfileImage(user);

		String url = s3ImageStorage.uploadFile(file);
		ProfileImage newImg = ProfileImage.builder()
			.user(user)
			.profileImageUrl(url)
			.build();
		return profileImageRepository.saveProfileImage(newImg);
	}

	@Override
	public ProfileImage uploadProfileImageFromUrl(String imageUrl, User user) {
		if (!StringUtils.hasText(imageUrl)) {
			throw new BusinessException(
				ErrorCode.INVALID_INPUT_VALUE,
				"프로필 이미지 URL이 없습니다."
			);
		}
		String url = s3ImageStorage.uploadFromUrl(imageUrl);
		ProfileImage pi = mapper.toProfileImage(user, url);
		return profileImageRepository.saveProfileImage(pi);
	}

	@Override
	@Transactional
	public ProfileImage updateProfileImage(MultipartFile newFile, User currentUser) {
		log.info(">> updateProfileImage 시작: userId={}, originalFilename={}", currentUser.getId(),
			newFile.getOriginalFilename());
		ProfileImage uploadProfileImage = uploadProfileImage(newFile, currentUser);
		log.info("<< updateProfileImage 완료: profileImageId={}, url={}", uploadProfileImage.getId(),
			uploadProfileImage.getProfileImageUrl());
		return uploadProfileImage;
	}

	@Override
	@Transactional(readOnly = true)
	public ProfileImage getProfileImageByUser(User user) {
		return profileImageRepository.findByUser(user)
			.orElseThrow(() -> new BusinessException(
				ErrorCode.ENTITY_NOT_FOUND,
				"프로필 이미지를 찾을 수 없습니다."
			));
	}

	// @Override
	// @Transactional
	// public void deleteProfileImage(User user) {
	// 	profileImageRepository.findByUser(user).ifPresent(pi -> {
	// 		validator.validateForDelete(pi);
	// 		s3ImageStorage.deleteByUrl(pi.getProfileImageUrl());
	// 		profileImageRepository.deleteProfileImageById(pi.getId());
	// 	});
	// }
	@Override
	@Transactional
	public void deleteProfileImage(User currentUser) {
		User user = userRepository.findByGithubId(currentUser.getGithubId())
			.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "사용자를 찾을 수 없습니다."));

		unlinkAndDeleteProfileImage(user);
	}

	private void unlinkAndDeleteProfileImage(User user) {
		ProfileImage img = user.getProfileImage();
		if (img == null)
			return;

		validator.validateForDelete(img);

		try {
			s3ImageStorage.deleteByUrl(img.getProfileImageUrl());
		} catch (BusinessException e) {
			log.warn("S3 삭제 실패(무시): {}", e.getMessage());
		}

		user.setProfileImage(null);
	}

}
