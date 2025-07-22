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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

/**
 * 리팩토링 예정
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ProfileImageServiceImpl implements ProfileImageService {

	private final S3ImageStorage s3ImageStorage;
	private final ProfileImageValidator validator;
	private final ProfileImageRepository repository;
	private final ProfileImageRepository profileImageRepository;
	private final @Qualifier("profileImageMapperImpl") ProfileImageMapper mapper;

	@Override
	@Transactional
	public ProfileImage uploadProfileImage(MultipartFile file, User user) {
		validator.validateFile(file);
		user.setProfileImage(null);
		profileImageRepository.deleteByUser(user);
		profileImageRepository.flush();
		String url = s3ImageStorage.uploadFile(file);
		ProfileImage newImg = ProfileImage.builder()
			.user(user)
			.profileImageUrl(url)
			.build();
		ProfileImage saved = profileImageRepository.saveProfileImage(newImg);
		user.setProfileImage(saved);

		return saved;
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
		return repository.saveProfileImage(pi);
	}

	@Override
	public ProfileImage updateProfileImage(MultipartFile newFile, User user) {
		deleteProfileImage(user);
		return uploadProfileImage(newFile, user);
	}

	@Override
	@Transactional(readOnly = true)
	public ProfileImage getProfileImageByUser(User user) {
		return repository.findByUser(user)
			.orElseThrow(() -> new BusinessException(
				ErrorCode.ENTITY_NOT_FOUND,
				"프로필 이미지를 찾을 수 없습니다."
			));
	}

	@Override
	public void deleteProfileImage(User user) {
		repository.findByUser(user).ifPresent(pi -> {
			validator.validateForDelete(pi);
			s3ImageStorage.deleteByUrl(pi.getProfileImageUrl());
			repository.deleteProfileImageById(pi.getId());
		});
	}
}
