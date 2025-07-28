package org.gamja.gamzatechblog.domain.post.service.port;

import java.time.LocalDateTime;
import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.dto.response.PostListResponse;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryPort {
	Page<PostListResponse> findAllPosts(Pageable pageable, List<String> tagNames);

	List<Post> findWeeklyPopularPosts(LocalDateTime since, int limit);

	Page<PostListResponse> findMyPosts(Pageable pageable, User currentUser);

	Page<PostListResponse> searchPostsByTitle(Pageable pageable, String keyword);
}
