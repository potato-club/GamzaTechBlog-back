package org.gamja.gamzatechblog.support.post;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public final class PostFixtures {
	private PostFixtures() {
	}

	public static User user(Long id) {
		return User.builder()
			.id(id)
			.email("user" + id + "@mail.com")
			.nickname("user" + id)
			.build();
	}

	public static GitHubRepo repo(User user, String name) {
		return GitHubRepo.builder()
			.user(user)
			.name(name)
			.githubUrl("https://github.com/" + user.getNickname() + "/" + name)
			.build();
	}

	public static Post post(Long id, User user, String title, String content) {
		return Post.builder()
			.id(id)
			.user(user)
			.title(title)
			.content(content)
			.build();
	}

	public static Post draft(User user, GitHubRepo githubRepo, String title, String content) {
		return Post.builder()
			.user(user)
			.githubRepo(githubRepo)
			.title(title)
			.content(content)
			.build();
	}
}
