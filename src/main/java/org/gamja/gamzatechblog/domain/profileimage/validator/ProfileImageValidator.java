package org.gamja.gamzatechblog.domain.profileimage.validator;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.domain.profileimage.exception.ProfileImageEmptyException;
import org.gamja.gamzatechblog.domain.profileimage.exception.ProfileImageInvalidTypeException;
import org.gamja.gamzatechblog.domain.profileimage.exception.ProfileImageSizeExceededException;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.profileimage.service.port.ProfileImageRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProfileImageValidator {

	private final ProfileImageRepository profileImageRepository;
	private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif");
	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

	public void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new ProfileImageEmptyException(ErrorCode.PROFILE_IMAGE_EMPTY);
		}
		if (file.getSize() > MAX_FILE_SIZE) {
			throw new ProfileImageSizeExceededException(ErrorCode.PROFILE_IMAGE_SIZE_EXCEEDED);
		}
		String filename = file.getOriginalFilename();
		String ext = filename != null && filename.contains(".")
			? filename.substring(filename.lastIndexOf('.') + 1).toLowerCase()
			: "";
		if (!ALLOWED_EXTENSIONS.contains(ext)) {
			throw new ProfileImageInvalidTypeException(ErrorCode.PROFILE_IMAGE_INVALID_TYPE);
		}
	}

	public void validateForDelete(ProfileImage img) {
		if (img == null || img.getProfileImageUrl() == null || img.getProfileImageUrl().isBlank()) {
			throw new ProfileImageEmptyException(ErrorCode.PROFILE_IMAGE_EMPTY);
		}
	}

	public Optional<ProfileImage> findExistingByUser(User user) {
		return profileImageRepository.findByUser(user);
	}

	public void validateUrl(String imageUrl) {
		if (!StringUtils.hasText(imageUrl)) {
			throw new ProfileImageEmptyException(ErrorCode.PROFILE_IMAGE_EMPTY);
		}
		URI uri;
		try {
			uri = URI.create(imageUrl);
		} catch (Exception e) {
			throw new ProfileImageInvalidTypeException(ErrorCode.PROFILE_IMAGE_INVALID_TYPE);
		}

		String host = uri.getHost() != null ? uri.getHost().toLowerCase() : "";

		if (host.endsWith("githubusercontent.com") || host.endsWith("github.com")) {
			return;
		}

		String path = uri.getPath();
		int lastSlash = path.lastIndexOf('/');
		if (lastSlash == -1) {
			throw new ProfileImageInvalidTypeException(ErrorCode.PROFILE_IMAGE_INVALID_TYPE);
		}
		String filename = path.substring(lastSlash + 1);

		int idx = filename.lastIndexOf('.');
		if (idx == -1) {
			throw new ProfileImageInvalidTypeException(ErrorCode.PROFILE_IMAGE_INVALID_TYPE);
		}
		String ext = filename.substring(idx + 1).toLowerCase();

		if (!ALLOWED_EXTENSIONS.contains(ext)) {
			throw new ProfileImageInvalidTypeException(ErrorCode.PROFILE_IMAGE_INVALID_TYPE);
		}
	}
}
