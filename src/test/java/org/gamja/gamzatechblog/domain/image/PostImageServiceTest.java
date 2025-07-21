package org.gamja.gamzatechblog.domain.image;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.gamja.gamzatechblog.domain.image.port.PostImageFakeRepository;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.postimage.model.entity.PostImage;
import org.gamja.gamzatechblog.support.image.ImageFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PostImageServiceTest {

	private PostImageService postImageService;
	private PostImageFakeRepository postImageRepository;
	private StubImageStorage stubStorage;

	@BeforeEach
	void setUp() {
		postImageRepository = new PostImageFakeRepository();
		stubStorage = new StubImageStorage();
		postImageService = new PostImageService(postImageRepository, stubStorage);
	}

	@Test
	@DisplayName("게시글 이미지 업로드 시 PostImage 엔티티가 저장된다")
	void uploadPostImage_shouldSaveEntity() {
		Post post = ImageFixtures.post(1L, ImageFixtures.user(1L));
		InputStream imageStream = new ByteArrayInputStream("dummy".getBytes());

		PostImage savedImage = postImageService.upload(post, imageStream, "post.png");

		assertNotNull(savedImage.getId(), "ID가 null이 아니어야 한다");
		assertEquals("https://stub/post.png", savedImage.getPostImageUrl());
		assertEquals(1, postImageRepository.findAll().size());
	}

	@Test
	@DisplayName("게시글 이미지 삭제 시 엔티티와 스토리지에서 함께 삭제된다")
	void deletePostImage_shouldRemoveEntityAndStorage() {
		Post post = ImageFixtures.post(1L, ImageFixtures.user(1L));
		PostImage existing = postImageService.upload(
			post,
			new ByteArrayInputStream("dummy".getBytes()),
			"delete.png"
		);

		postImageService.delete(existing.getId());

		assertTrue(postImageRepository.findAll().isEmpty());
		assertTrue(stubStorage.isDeleted("https://stub/delete.png"));
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
