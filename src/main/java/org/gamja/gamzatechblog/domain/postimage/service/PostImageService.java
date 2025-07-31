package org.gamja.gamzatechblog.domain.postimage.service;

import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.springframework.web.multipart.MultipartFile;

public interface PostImageService {
	String saveTemporaryImage(MultipartFile file);

	void syncImages(Post post);

	List<String> getImageUrls(Post post);

	void syncDatabaseRecords(Post post);
}
