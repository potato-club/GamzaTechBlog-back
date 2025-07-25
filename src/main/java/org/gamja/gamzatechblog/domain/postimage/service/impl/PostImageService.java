package org.gamja.gamzatechblog.domain.postimage.service.impl;

import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.springframework.web.multipart.MultipartFile;

public interface PostImageService {
	String uploadImage(MultipartFile file);

	void syncImages(Post post);

	List<String> getImageUrls(Post post);
}
