package org.gamja.gamzatechblog.domain.like.service.port;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.like.model.entity.Like;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public interface LikeRepository {
	Optional<Like> findByUserAndPost(User user, Post post);

	void deleteByUserAndPost(User user, Post post);

	int countByUser(User user);

	Like saveLike(Like like);
}
