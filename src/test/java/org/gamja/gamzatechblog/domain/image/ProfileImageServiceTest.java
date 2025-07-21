package org.gamja.gamzatechblog.domain.image;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.gamja.gamzatechblog.domain.image.port.ProfileImageFakeRepository;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.support.image.ImageFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProfileImageServiceTest {

	private ProfileImageService profileImageService;
	private ProfileImageFakeRepository profileImageRepository;
	private StubImageStorage stubStorage;

	@BeforeEach
	void setUp() {
		profileImageRepository = new ProfileImageFakeRepository();
		stubStorage = new StubImageStorage();
		profileImageService = new ProfileImageService(profileImageRepository, stubStorage);
	}

	@Test
	@DisplayName("프로필 이미지 첫 업로드 시 ProfileImage 엔티티가 저장된다")
	void uploadFirstProfileImage_shouldSaveEntity() {
		User user = ImageFixtures.user(1L);
		InputStream imageStream = new ByteArrayInputStream("dummy".getBytes());

		ProfileImage savedProfile = profileImageService.upload(user, imageStream, "avatar.png");

		assertNotNull(savedProfile.getId());
		assertEquals("https://stub/avatar.png", savedProfile.getProfileImageUrl());
		assertTrue(profileImageRepository.findById(savedProfile.getId()).isPresent());
	}

	@Test
	@DisplayName("프로필 이미지 교체 시 기존 이미지 삭제 후 새 엔티티만 남는다")
	void replaceProfileImage_shouldDeleteOldAndSaveNew() {
		User user = ImageFixtures.user(1L);
		profileImageService.upload(
			user,
			new ByteArrayInputStream("old".getBytes()),
			"old.png"
		);

		ProfileImage newProfile = profileImageService.upload(
			user,
			new ByteArrayInputStream("new".getBytes()),
			"new.png"
		);

		assertEquals("https://stub/new.png", newProfile.getProfileImageUrl());
		assertEquals(1, profileImageRepository.findAll().size());
		assertTrue(stubStorage.isDeleted("https://stub/old.png"));
	}

	private static class StubImageStorage implements ImageStorage {
		private final java.util.Set<String> deletedUrls = new java.util.HashSet<>();

		@Override
		public String upload(InputStream stream, String originalFileName) {
			return "https://stub/" + originalFileName;
		}

		@Override
		public void delete(String url) {
			deletedUrls.add(url);
		}

		boolean isDeleted(String url) {
			return deletedUrls.contains(url);
		}
	}
}
