package org.gamja.gamzatechblog.domain.profileimage.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.gamja.gamzatechblog.common.port.s3.S3ImageStorage;
import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.profileimage.model.mapper.ProfileImageMapper;
import org.gamja.gamzatechblog.domain.profileimage.service.ProfileImageService;
import org.gamja.gamzatechblog.domain.profileimage.service.port.ProfileImageRepository;
import org.gamja.gamzatechblog.domain.profileimage.validator.ProfileImageValidator;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileImageServiceImpl implements ProfileImageService {

	private final S3ImageStorage s3ImageStorage;
	private final ProfileImageValidator profileImageValidator;
	private final ProfileImageMapper profileImageMapper;
	private final ProfileImageRepository profileImageRepository;

	@Override
	public ProfileImage uploadProfileImage(MultipartFile file, User user) {
		profileImageValidator.validateFile(file);

		String imageUrl = uploadToS3(file);

		ProfileImage profileImage = profileImageMapper.toProfileImage(user, imageUrl);
		return profileImageRepository.saveProfileImage(profileImage);
	}

	@Override
	public ProfileImage updateProfileImage(MultipartFile newFile, User user) {
		profileImageRepository.findByUser(user).ifPresent(pi -> deleteProfileImage(user));

		return uploadProfileImage(newFile, user);
	}

	@Override
	@Transactional(readOnly = true)
	public ProfileImage getProfileImageByUser(User user) {
		return profileImageRepository.findByUser(user)
			.orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "프로필 이미지를 찾을 수 없습니다."));
	}

	@Override
	public void deleteProfileImage(User user) {
		ProfileImage existing = profileImageRepository.findByUser(user)
			.orElseThrow(() -> new BusinessException(
				ErrorCode.ENTITY_NOT_FOUND, "프로필 이미지를 찾을 수 없습니다.")
			);
		profileImageValidator.validateForDelete(existing);
		try {
			s3ImageStorage.delete(existing.getProfileImageUrl());
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.PROFILE_IMAGE_DELETE_FAILED);
		}

		profileImageRepository.deleteProfileImageById(existing.getId());
	}

	private String uploadToS3(MultipartFile file) {
		try (InputStream inputStream = file.getInputStream()) {
			return s3ImageStorage.upload(inputStream, file.getOriginalFilename());
		} catch (IOException e) {
			throw new BusinessException(ErrorCode.PROFILE_IMAGE_UPLOAD_FAILED);
		}
	}
}