package org.gamja.gamzatechblog.domain.like.service.port;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.like.model.entity.Like;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public class LikeFakeRepository implements LikeRepository {

	private final Map<String, Like> likeStorage = new HashMap<>();
	private Long nextLikeId = 1L;

	@Override
	public Optional<Like> findByUserAndPost(User user, Post post) {
		String key = createUserPostKey(user, post);
		return Optional.ofNullable(likeStorage.get(key));
	}

	@Override
	public void deleteByUserAndPost(User user, Post post) {
		String key = createUserPostKey(user, post);
		likeStorage.remove(key);
	}

	@Override
	public int countByUser(User user) {
		return (int)likeStorage.values().stream()
			.filter(like -> isLikeFromUser(like, user))
			.count();
	}

	@Override
	public Like saveLike(Like like) {
		Like toSave = isNewLike(like) ? createLikeWithNewId(like) : like;
		String key = createUserPostKey(toSave.getUser(), toSave.getPost());
		likeStorage.put(key, toSave); // 동일 user-post면 덮어쓰기
		return toSave;
	}

	@Override
	public boolean existsByUserAndPost(User user, Post post) {
		String key = createUserPostKey(user, post);
		return likeStorage.containsKey(key);
	}

	/* ==== 내부 유틸 ==== */

	private boolean isNewLike(Like like) {
		return like.getId() == null;
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
		Objects.requireNonNull(user, "user must not be null");
		Objects.requireNonNull(post, "post must not be null");
		if (user.getId() == null || post.getId() == null) {
			throw new IllegalArgumentException("user.id and post.id must not be null");
		}
		return "user:%d:post:%d".formatted(user.getId(), post.getId());
	}
}
