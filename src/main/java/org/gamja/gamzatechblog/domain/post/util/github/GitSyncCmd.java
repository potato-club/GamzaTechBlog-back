package org.gamja.gamzatechblog.domain.post.util.github;

import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;

import lombok.Builder;

@Builder
public record GitSyncCmd(
	String token,
	String oldTitle,
	List<String> oldTags,
	Post post,
	List<String> tags,
	GitAction action,
	String commitMessage
) {
	public static GitSyncCmd add(String token, Post post, List<String> tags, String commitMessage) {
		return GitSyncCmd.builder()
			.token(token)
			.post(post)
			.tags(tags)
			.action(GitAction.ADD)
			.commitMessage(commitMessage)
			.build();
	}

	public static GitSyncCmd update(String token, String oldTitle, List<String> oldTags,
		Post post, List<String> tags, String commitMessage) {
		return GitSyncCmd.builder()
			.token(token)
			.oldTitle(oldTitle)
			.oldTags(oldTags)
			.post(post)
			.tags(tags)
			.action(GitAction.UPDATE)
			.commitMessage(commitMessage)
			.build();
	}
}
