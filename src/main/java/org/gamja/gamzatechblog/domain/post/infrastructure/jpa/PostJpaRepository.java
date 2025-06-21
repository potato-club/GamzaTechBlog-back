package org.gamja.gamzatechblog.domain.post.infrastructure.jpa;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostJpaRepository extends JpaRepository<Post, Long> {
	@EntityGraph(attributePaths = {"user", "postTags", "postTags.tag"})
	Optional<Post> findById(Long postId);

	Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	int countByUser(User user);
}
