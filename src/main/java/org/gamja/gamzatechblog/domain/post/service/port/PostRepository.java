package org.gamja.gamzatechblog.domain.post.service.port;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository {
	Optional<Post> findById(Long postId);

	Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	int countByUser(User user);

	Post save(Post post);

	void delete(Post post);

	List<Post> findTop3ByCreatedAtAfterOrderByLikesCountDesc(LocalDateTime dateTime);
}
