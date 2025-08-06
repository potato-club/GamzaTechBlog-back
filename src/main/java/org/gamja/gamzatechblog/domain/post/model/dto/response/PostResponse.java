package org.gamja.gamzatechblog.domain.post.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;

public record PostResponse(
	Long postId,
	String authorGithubId,
	String repositoryName,
	String title,
	String content,
	List<String> tags,
	String commitMessage,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static PostResponse fromPostEntity(Post post) {
		return new PostResponse(
			post.getId(),
			post.getUser().getGithubId(),
			post.getGithubRepo().getName(),
			post.getTitle(),
			post.getContent(),
			post.getPostTags().stream()
				.map(pt -> pt.getTag().getTagName())
				.toList(),
			post.getCommitMessage(),
			post.getCreatedAt(),
			post.getUpdatedAt()
		);
	}
}
