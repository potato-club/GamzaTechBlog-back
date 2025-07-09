package org.gamja.gamzatechblog.domain.like.infrastructure;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.like.model.entity.Like;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {
	Optional<Like> findByUserAndPost(User user, Post post);

	void deleteByUserAndPost(User user, Post post);

	int countByUser(User user);
}