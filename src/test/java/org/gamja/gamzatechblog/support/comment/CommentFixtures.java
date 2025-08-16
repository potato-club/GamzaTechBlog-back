package org.gamja.gamzatechblog.support.comment;

import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public final class CommentFixtures {
	private CommentFixtures() {
	}

	public static User user(Long id) {
		return User.builder()
			.id(id)
			.email("u" + id + "@mail.com")
			.nickname("user" + id)
			.build();
	}

	public static Post post(Long id, User user) {
		return Post.builder()
			.id(id)
			.user(user)
			.title("title-" + id)
			.content("content-" + id)
			.build();
	}

	public static Comment comment(Post post, User user, Comment parent, String content) {
		return Comment.builder()
			.post(post)
			.user(user)
			.parent(parent)
			.content(content)
			.build();
	}
}
