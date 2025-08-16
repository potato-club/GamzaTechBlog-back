package org.gamja.gamzatechblog.domain.image;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.gamja.gamzatechblog.common.port.s3.S3ImageStorage;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.profileimage.model.mapper.ProfileImageMapper;
import org.gamja.gamzatechblog.domain.profileimage.service.impl.ProfileImageServiceImpl;
import org.gamja.gamzatechblog.domain.profileimage.service.port.ProfileImageRepository;
import org.gamja.gamzatechblog.domain.profileimage.validator.ProfileImageValidator;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.service.port.UserRepository;
import org.gamja.gamzatechblog.support.image.ImageFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class ProfileImageServiceTest {

	private ProfileImageServiceImpl profileImageService;
	private ProfileImageRepository profileImageRepository;
	private UserRepository userRepository;
	private ProfileImageValidator validator;
	private ProfileImageMapper mapper;
	private StubS3ImageStorage stubStorage;

	@BeforeEach
	void setUp() {
		profileImageRepository = mock(ProfileImageRepository.class);
		userRepository = mock(UserRepository.class);
		validator = mock(ProfileImageValidator.class);
		mapper = mock(ProfileImageMapper.class);

		stubStorage = new StubS3ImageStorage();

		when(mapper.toProfileImage(any(User.class), anyString()))
			.thenAnswer(inv -> ProfileImage.builder()
				.user(inv.getArgument(0, User.class))
				.profileImageUrl(inv.getArgument(1, String.class))
				.build());

		profileImageService = new ProfileImageServiceImpl(
			stubStorage,
			validator,
			profileImageRepository,
			mapper,
			userRepository
		);
	}

	@Test
	@DisplayName("프로필 이미지 첫 업로드 시 User에 프로필 이미지가 연결된다")
	void uploadFirstProfileImage_shouldSaveEntity() {
		User user = ImageFixtures.user(1L);
		when(userRepository.findByGithubId(user.getGithubId()))
			.thenReturn(Optional.of(user));
		when(userRepository.saveUser(user)).thenReturn(user);

		MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "dummy".getBytes());

		ProfileImage savedProfile = profileImageService.uploadProfileImage(file, user);

		assertNotNull(savedProfile);
		assertEquals("https://stub/profile-images/avatar.png", savedProfile.getProfileImageUrl());
		verify(validator, times(1)).validateFile(file);
	}

	@Test
	@DisplayName("프로필 이미지 교체 시 기존 이미지는 삭제 처리된다")
	void replaceProfileImage_shouldDeleteOldAndSaveNew() {
		User user = ImageFixtures.user(1L);
		ProfileImage old = ProfileImage.builder().profileImageUrl("https://stub/profile-images/old.png").build();
		user.changeProfileImage(old);

		when(userRepository.findByGithubId(user.getGithubId()))
			.thenReturn(Optional.of(user));
		when(userRepository.saveUser(user)).thenReturn(user);

		MockMultipartFile newFile = new MockMultipartFile("file", "new.png", "image/png", "new".getBytes());

		ProfileImage newProfile = profileImageService.uploadProfileImage(newFile, user);

		assertEquals("https://stub/profile-images/new.png", newProfile.getProfileImageUrl());
		assertTrue(stubStorage.isDeleted("https://stub/profile-images/old.png"));
		verify(validator, times(1)).validateFile(newFile);
	}

	private static class StubS3ImageStorage implements S3ImageStorage {
		private final Set<String> deletedUrls = new HashSet<>();

		@Override
		public String uploadStream(java.io.InputStream stream, String originalFileName, String prefix) {
			return "https://stub/" + prefix + "/" + originalFileName;
		}

		@Override
		public String uploadFile(org.springframework.web.multipart.MultipartFile file, String prefix) {
			return "https://stub/" + prefix + "/" + file.getOriginalFilename();
		}

		@Override
		public String uploadFromUrl(String imageUrl, String prefix) {
			return "https://stub/" + prefix + "/" + imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
		}

		@Override
		public void deleteByUrl(String fileUrl) {
			deletedUrls.add(fileUrl);
		}

		@Override
		public String move(String sourceUrl, String destPrefix) {
			deletedUrls.add(sourceUrl);
			return "https://stub/" + destPrefix + "/" + sourceUrl.substring(sourceUrl.lastIndexOf('/') + 1);
		}

		boolean isDeleted(String url) {
			return deletedUrls.contains(url);
		}
	}
}
