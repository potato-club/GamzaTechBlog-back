package org.gamja.gamzatechblog.domain.post.repository;

import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
	Page<Post> findAllPosts(Pageable pageable, List<String> tagNames);
}
