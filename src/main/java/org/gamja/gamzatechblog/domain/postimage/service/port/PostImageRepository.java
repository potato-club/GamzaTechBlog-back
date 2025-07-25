package org.gamja.gamzatechblog.domain.postimage.service.port;

import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.postimage.model.entity.PostImage;

public interface PostImageRepository {
	PostImage save(PostImage postImage);

	List<PostImage> findAllByPostOrderById(Post post);

	void deleteAllByPost(Post post);

	List<PostImage> saveAll(List<PostImage> postImages);
}
