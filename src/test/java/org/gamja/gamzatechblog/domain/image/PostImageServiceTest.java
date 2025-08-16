package org.gamja.gamzatechblog.domain.image;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.gamja.gamzatechblog.common.port.s3.S3ImageStorage;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.postimage.model.entity.PostImage;
import org.gamja.gamzatechblog.domain.postimage.service.impl.PostImageServiceImpl;
import org.gamja.gamzatechblog.domain.postimage.service.port.PostImageRepository;
import org.gamja.gamzatechblog.domain.postimage.service.util.PostImageServiceUtil;
import org.gamja.gamzatechblog.support.image.ImageFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostImageServiceTest {

	@Mock
	S3ImageStorage s3;
	@Mock
	PostImageRepository repo;
	@Mock
	PostImageServiceUtil util;

	@InjectMocks
	PostImageServiceImpl service;

	@Captor
	ArgumentCaptor<List<PostImage>> imagesCaptor;

	@Test
	@DisplayName("syncImages: tmp 이미지가 post-images/{postId}로 이동되고 DB 레코드가 갱신된다")
	void syncImages_movesAndPersists() {
		Post post = ImageFixtures.post(10L, ImageFixtures.user(1L));
		post.setContent("""
			<p>text</p>
			<img src="/tmp_images/a.png"/>
			<img src="/etc/c.png"/>
			<img src="/tmp_images/b.jpg"/>
			""");

		when(util.extractImageUrls(anyString()))
			.thenReturn(List.of("/tmp_images/a.png", "/etc/c.png", "/tmp_images/b.jpg"))  // move 전
			.thenReturn(List.of("https://cdn/post-images/10/a.png", "/etc/c.png",
				"https://cdn/post-images/10/b.jpg")); // move 후

		when(s3.move("/tmp_images/a.png", "post-images/10")).thenReturn("https://cdn/post-images/10/a.png");
		when(s3.move("/tmp_images/b.jpg", "post-images/10")).thenReturn("https://cdn/post-images/10/b.jpg");

		when(repo.findAllByPostOrderById(post)).thenReturn(List.of()); // 기존 레코드 없음

		service.syncImages(post);

		verify(s3).move("/tmp_images/a.png", "post-images/10");
		verify(s3).move("/tmp_images/b.jpg", "post-images/10");
		verify(repo).deleteAllByPost(post);
		verify(repo).saveAll(imagesCaptor.capture());

		List<PostImage> saved = imagesCaptor.getValue();
		assertThat(saved).hasSize(3);
		assertThat(saved).extracting(PostImage::getPostImageUrl)
			.containsExactlyInAnyOrder(
				"https://cdn/post-images/10/a.png",
				"https://cdn/post-images/10/b.jpg",
				"/etc/c.png"
			);

		assertThat(post.getContent())
			.doesNotContain("/tmp_images/")
			.contains("post-images/10");
	}

	@Test
	@DisplayName("deleteImagesForPost: S3와 레포에서 모두 삭제한다")
	void deleteImagesForPost_deletes() {
		Post post = ImageFixtures.post(7L, ImageFixtures.user(1L));
		when(repo.findAllByPostOrderById(post)).thenReturn(List.of(
			PostImage.builder().post(post).postImageUrl("u1").build(),
			PostImage.builder().post(post).postImageUrl("u2").build()
		));

		service.deleteImagesForPost(post);

		verify(s3).deleteByUrl("u1");
		verify(s3).deleteByUrl("u2");
		verify(repo).deleteAllByPost(post);
	}

	@Test
	@DisplayName("getImageUrls: Post의 이미지 URL 목록을 반환한다")
	void getImageUrls_returnsUrls() {
		Post post = ImageFixtures.post(3L, ImageFixtures.user(1L));
		when(repo.findAllByPostOrderById(post)).thenReturn(List.of(
			PostImage.builder().post(post).postImageUrl("x").build(),
			PostImage.builder().post(post).postImageUrl("y").build()
		));

		List<String> urls = service.getImageUrls(post);

		assertThat(urls).containsExactly("x", "y");
		verify(repo).findAllByPostOrderById(post);
	}
}
