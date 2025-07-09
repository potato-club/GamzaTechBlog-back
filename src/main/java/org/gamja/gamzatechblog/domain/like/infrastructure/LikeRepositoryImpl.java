package org.gamja.gamzatechblog.domain.like.infrastructure;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.like.model.entity.Like;
import org.gamja.gamzatechblog.domain.like.service.port.LikeRepository;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LikeRepositoryImpl implements LikeRepository {
	private final LikeJpaRepository likeJpaRepository;

	@Override
	public Optional<Like> findByUserAndPost(User user, Post post) {
		return likeJpaRepository.findByUserAndPost(user, post);
	}

	@Override
	public void deleteByUserAndPost(User user, Post post) {
		likeJpaRepository.deleteByUserAndPost(user, post);
	}

	@Override
	public int countByUser(User user) {
		return likeJpaRepository.countByUser(user);
	}

	@Override
	public Like saveLike(Like like) {
		return likeJpaRepository.save(like);
	}
}