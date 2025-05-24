package org.gamja.gamzatechblog.domain.post.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponse {
	private Long postId;
	private String authorGithubId;
	private String repositoryName;
	private String title;
	private String content;
	private List<String> tags;
	private String commitMessage;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
