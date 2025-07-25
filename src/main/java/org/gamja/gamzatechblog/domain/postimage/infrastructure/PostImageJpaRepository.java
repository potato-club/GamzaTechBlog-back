package org.gamja.gamzatechblog.domain.postimage.infrastructure;

import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.postimage.model.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageJpaRepository extends JpaRepository<PostImage, Long> {
	List<PostImage> findAllByPostOrderById(Post post);

	void deleteAllByPost(Post post);
}
