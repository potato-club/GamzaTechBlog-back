package org.gamja.gamzatechblog.support.image;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.postimage.model.entity.PostImage;
import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public final class ImageFixtures {

	private ImageFixtures() {
	}

	public static User user(Long id) {
		return User.builder()
			.id(id)
			.email("user" + id + "@example.com")
			.nickname("유저" + id)
			.build();
	}

	public static Post post(Long id, User user) {
		return Post.builder()
			.id(id)
			.user(user)
			.title("테스트 글 " + id)
			.content("본문 내용")
			.build();
	}

	public static PostImage postImage(Post post, String url) {
		return PostImage.builder()
			.post(post)
			.postImageUrl(url)
			.build();
	}

	public static ProfileImage profileImage(User user, String url) {
		return ProfileImage.builder()
			.user(user)
			.profileImageUrl(url)
			.build();
	}
}
