package org.gamja.gamzatechblog.support.like;

import org.gamja.gamzatechblog.domain.like.model.entity.Like;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public class LikeFixtures {

	public static final User USER = User.builder()
		.id(1L)
		.email("test@test.com")
		.nickname("testuser")
		.build();

	public static final Post POST_1 = Post.builder()
		.id(1L)
		.title("Test Post 1")
		.content("Test Content 1")
		.user(USER)
		.build();

	public static final Post POST_2 = Post.builder()
		.id(2L)
		.title("Test Post 2")
		.content("Test Content 2")
		.user(USER)
		.build();

	public static final Like LIKE_POST_1 = Like.builder()
		.user(USER)
		.post(POST_1)
		.build();

	public static Like createLike(User user, Post post) {
		return Like.builder()
			.user(user)
			.post(post)
			.build();
	}
}