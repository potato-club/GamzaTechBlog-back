package org.gamja.gamzatechblog.domain.post.repository;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
	@EntityGraph(attributePaths = {"user", "postTags", "postTags.tag"})
		//leftJoin 쿼리 엔티티임
	Optional<Post> findById(Long postId);
}