package org.gamja.gamzatechblog.domain.post.repository;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
	@EntityGraph(attributePaths = {"user", "postTags", "postTags.tag"})
		//leftJoin 쿼리 엔티티임
	Optional<Post> findById(Long postId);

	Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	int countByUser(User user);

}