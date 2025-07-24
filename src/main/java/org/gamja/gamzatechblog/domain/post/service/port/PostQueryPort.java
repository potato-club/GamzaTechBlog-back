package org.gamja.gamzatechblog.domain.post.service.port;

import java.time.LocalDateTime;
import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryPort {
	Page<Post> findAllPosts(Pageable pageable, List<String> tagNames);

	List<Post> findWeeklyPopularPosts(LocalDateTime since, int limit);
}
