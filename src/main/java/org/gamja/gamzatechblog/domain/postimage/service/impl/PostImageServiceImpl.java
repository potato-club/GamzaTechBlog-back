package org.gamja.gamzatechblog.domain.postimage.service.impl;

import java.util.List;

import org.gamja.gamzatechblog.common.port.s3.S3ImageStorage;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.postimage.model.entity.PostImage;
import org.gamja.gamzatechblog.domain.postimage.service.PostImageService;
import org.gamja.gamzatechblog.domain.postimage.service.port.PostImageRepository;
import org.gamja.gamzatechblog.domain.postimage.service.util.PostImageServiceUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostImageServiceImpl implements PostImageService {
	private final S3ImageStorage s3ImageStorage;
	private final PostImageRepository postImageRepository;
	private final PostImageServiceUtil postImageServiceUtil;

	@Override
	public String uploadImage(MultipartFile file) {
		return s3ImageStorage.uploadFile(file, "tmp_images");
	}

	@Override
	@Transactional
	public void syncImages(Post post) {
		List<String> oldUrls = postImageRepository.findAllByPostOrderById(post)
			.stream()
			.map(PostImage::getPostImageUrl)
			.toList();

		String content = post.getContent();
		List<String> tmpUrls = postImageServiceUtil.extractImageUrls(content).stream()
			.filter(url -> url.contains("/tmp_images/"))
			.toList();

		String postPrefix = "post_images/" + post.getId();
		for (String tmpUrl : tmpUrls) {
			String movedUrl = s3ImageStorage.move(tmpUrl, postPrefix);
			content = content.replace(tmpUrl, movedUrl);
		}
		post.setContent(content);

		List<String> newUrls = postImageServiceUtil.extractImageUrls(content);

		oldUrls.stream()
			.filter(url -> !newUrls.contains(url))
			.forEach(s3ImageStorage::deleteByUrl);

		postImageRepository.deleteAllByPost(post);
		List<PostImage> postImages = newUrls.stream()
			.map(url -> PostImage.builder()
				.post(post)
				.postImageUrl(url)
				.build())
			.toList();
		postImageRepository.saveAll(postImages);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getImageUrls(Post post) {
		return postImageRepository.findAllByPostOrderById(post).stream()
			.map(PostImage::getPostImageUrl)
			.toList();
	}
}
