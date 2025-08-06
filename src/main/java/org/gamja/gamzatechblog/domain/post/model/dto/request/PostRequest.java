package org.gamja.gamzatechblog.domain.post.model.dto.request;

import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record PostRequest(
	@NotBlank(message = "제목은 필수입니다")
	String title,

	@NotBlank(message = "본문은 필수입니다")
	String content,

	@NotEmpty(message = "태그를 하나 이상 선택하세요")
	List<String> tags,

	String commitMessage
) {
	public Post toPostEntity(User currentUser, GitHubRepo repo) {
		return Post.builder()
			.user(currentUser)
			.githubRepo(repo)
			.title(this.title)
			.content(this.content)
			.commitMessage(this.commitMessage)
			.build();
	}
}