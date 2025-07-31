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
	public String saveTemporaryImage(MultipartFile file) {
		return s3ImageStorage.uploadFile(file, "tmp_images");
	}

	@Override
	public void syncImages(Post post) {
		String updatedContent = moveTemporaryToPost(post.getContent(), post.getId());
		post.setContent(updatedContent);
		syncDatabaseRecords(post);
	}

	private String moveTemporaryToPost(String content, Long postId) {
		List<String> tmpUrls = postImageServiceUtil.extractImageUrls(content).stream()
			.filter(u -> u.contains("/tmp_images/"))
			.toList();

		String prefix = "post-images/" + postId;
		String result = content;

		for (String tmpUrl : tmpUrls) {
			String moved = s3ImageStorage.move(tmpUrl, prefix);
			result = result.replace(tmpUrl, moved);
		}
		return result;
	}

	@Override
	@Transactional
	public void syncDatabaseRecords(Post post) {
		List<String> oldUrls = postImageRepository
			.findAllByPostOrderById(post)
			.stream().map(PostImage::getPostImageUrl).toList();

		List<String> newUrls = postImageServiceUtil.extractImageUrls(post.getContent());

		// 사용되지 않는 URL 삭제
		oldUrls.stream()
			.filter(url -> !newUrls.contains(url))
			.forEach(s3ImageStorage::deleteByUrl);

		// 레코드 교체
		postImageRepository.deleteAllByPost(post);
		List<PostImage> entities = newUrls.stream()
			.map(u -> PostImage.builder().post(post).postImageUrl(u).build())
			.toList();
		postImageRepository.saveAll(entities);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getImageUrls(Post post) {
		return postImageRepository.findAllByPostOrderById(post).stream()
			.map(PostImage::getPostImageUrl)
			.toList();
	}
}
