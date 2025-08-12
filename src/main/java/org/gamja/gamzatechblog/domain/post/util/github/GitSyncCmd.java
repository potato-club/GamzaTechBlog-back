package org.gamja.gamzatechblog.domain.post.util.github;

import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record GitSyncCmd(
	@NotBlank String token,
	String oldTitle,
	List<String> oldTags,
	@NotNull Post post,
	@NotNull List<String> tags,
	@NotNull GitAction action,
	String commitMessage,
	@NotBlank String owner
) {
	public static GitSyncCmd add(String token, Post post, List<String> tags, String commitMessage, String owner) {
		return GitSyncCmd.builder()
			.token(token)
			.post(post)
			.tags(tags)
			.action(GitAction.ADD)
			.commitMessage(commitMessage).owner(owner)
			.build();
	}

	public static GitSyncCmd update(String token, String oldTitle, List<String> oldTags,
		Post post, List<String> tags, String commitMessage, String owner) {
		return GitSyncCmd.builder()
			.token(token)
			.oldTitle(oldTitle)
			.oldTags(oldTags)
			.post(post).tags(tags)
			.action(GitAction.UPDATE)
			.commitMessage(commitMessage).owner(owner)
			.build();
	}
}
