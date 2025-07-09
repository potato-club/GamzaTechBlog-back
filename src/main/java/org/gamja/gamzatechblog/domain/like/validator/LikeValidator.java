package org.gamja.gamzatechblog.domain.like.validator;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.domain.like.exception.AlreadyLikedException;
import org.gamja.gamzatechblog.domain.like.exception.LikeNotFoundException;
import org.gamja.gamzatechblog.domain.like.service.port.LikeRepository;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LikeValidator {

	private final LikeRepository likeRepository;

	public void validateNotAlreadyLiked(User user, Post post) {
		likeRepository.findByUserAndPost(user, post)
			.ifPresent(l -> {
				throw new AlreadyLikedException(ErrorCode.ALREADY_LIKED);
			});
	}

	public void validateExists(User user, Post post) {
		likeRepository.findByUserAndPost(user, post)
			.orElseThrow(() -> new LikeNotFoundException(ErrorCode.LIKE_NOT_FOUND));
	}
}
