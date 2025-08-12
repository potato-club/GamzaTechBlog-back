package org.gamja.gamzatechblog.domain.post.util.github;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record GitDeleteCmd(
	@NotBlank String token,
	@NotBlank String owner,
	@Positive long postId,
	@NotBlank String prevTitle,
	@NotNull List<String> prevTags,
	String commitMessage
) {
	public GitDeleteCmd {
		prevTags = List.copyOf(prevTags);
	}

	public static GitDeleteCmd forDeletion(
		String token,
		String owner,
		long postId,
		String prevTitle,
		List<String> prevTags,
		String commitMessage
	) {
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
