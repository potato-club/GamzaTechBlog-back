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
		return s3ImageStorage.uploadFile(file, "post-images");
	}

	@Override
	@Transactional
	public void syncImages(Post post) {
		List<String> oldUrls = postImageRepository.findAllByPostOrderById(post).stream()
			.map(PostImage::getPostImageUrl)
			.toList();

		String newContent = postImageServiceUtil.replaceAndUploadNewImages(post, post.getContent());
		post.setContent(newContent);

		List<String> newUrls = postImageServiceUtil.extractImageUrls(newContent);

		List<String> toDelete = oldUrls.stream()
			.filter(url -> !newUrls.contains(url))
			.toList();
		toDelete.forEach(url -> {
			s3ImageStorage.deleteByUrl(url);
		});
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
