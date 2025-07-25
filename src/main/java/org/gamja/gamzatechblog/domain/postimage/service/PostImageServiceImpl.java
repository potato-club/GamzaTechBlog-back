package org.gamja.gamzatechblog.domain.postimage.service;

import java.util.List;

import org.gamja.gamzatechblog.common.port.s3.S3ImageStorage;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.postimage.model.entity.PostImage;
import org.gamja.gamzatechblog.domain.postimage.service.impl.PostImageService;
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
	private final PostImageRepository postImageJpaRepository;
	private final PostImageServiceUtil postImageServiceUtil;

	@Override
	public String uploadImage(MultipartFile file) {
		return s3ImageStorage.uploadFile(file);
	}

	@Override
	@Transactional
	public void syncImages(Post post) {
		String newContent = postImageServiceUtil.replaceAndUploadAllImages(post, post.getContent());
		post.setContent(newContent);

		List<String> urls = postImageServiceUtil.extractImageUrls(newContent);
		postImageJpaRepository.deleteAllByPost(post);
		urls.forEach(url -> {
			postImageJpaRepository.postImageSave(
				PostImage.builder()
					.post(post)
					.postImageUrl(url)
					.build()
			);
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getImageUrls(Post post) {
		return postImageJpaRepository.findAllByPostOrderById(post).stream()
			.map(PostImage::getPostImageUrl)
			.toList();
	}
}
