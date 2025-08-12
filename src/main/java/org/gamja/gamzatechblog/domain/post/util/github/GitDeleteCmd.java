package org.gamja.gamzatechblog.domain.post.util.github;

import java.util.List;

import lombok.Builder;

@Builder
public record GitDeleteCmd(
	String token,
	String owner,
	long postId,
	String prevTitle,
	List<String> prevTags,
	String commitMessage
) {
	public static GitDeleteCmd of(String token, String owner, long postId,
		String prevTitle, List<String> prevTags, String commitMessage) {
		return GitDeleteCmd.builder()
			.token(token)
			.owner(owner)
			.postId(postId)
			.prevTitle(prevTitle)
			.prevTags(prevTags)
			.commitMessage(commitMessage)
			.build();
	}
}
