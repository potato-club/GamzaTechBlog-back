package org.gamja.gamzatechblog.domain.like.service.port;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.like.model.entity.Like;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public class LikeFakeRepository implements LikeRepository {

	private final Map<String, Like> likeStorage = new HashMap<>();
	private Long nextLikeId = 1L;

	@Override
	public Optional<Like> findByUserAndPost(User user, Post post) {
		String userPostKey = createUserPostKey(user, post);
		return Optional.ofNullable(likeStorage.get(userPostKey));
	}

	@Override
	public void deleteByUserAndPost(User user, Post post) {
		String userPostKey = createUserPostKey(user, post);
		likeStorage.remove(userPostKey);
	}

	@Override
	public int countByUser(User user) {
		return (int)likeStorage.values().stream()
			.filter(like -> isLikeFromUser(like, user))
			.count();
	}

	@Override
	public Like saveLike(Like like) {
		Like likeToSave = createLikeWithIdIfNeeded(like);
		String userPostKey = createUserPostKey(likeToSave.getUser(), likeToSave.getPost());
		likeStorage.put(userPostKey, likeToSave);
		return likeToSave;
	}

	private Like createLikeWithIdIfNeeded(Like like) {
		if (isNewLike(like)) {
			return createLikeWithNewId(like);
		}
		return like;
	}

	private boolean isNewLike(Like like) {
		return like.getId() == null || like.getId() == 0;
	}

	private Like createLikeWithNewId(Like like) {
		return Like.builder()
			.id(nextLikeId++)
			.user(like.getUser())
			.post(like.getPost())
			.build();
	}

	private boolean isLikeFromUser(Like like, User user) {
		return like.getUser().equals(user);
	}

	private String createUserPostKey(User user, Post post) {
		return user.getId() + "_" + post.getId();
	}
}