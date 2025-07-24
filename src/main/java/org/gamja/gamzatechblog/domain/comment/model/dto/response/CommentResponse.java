package org.gamja.gamzatechblog.domain.comment.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {
	private Long commentId;
	private String writer;
	private String writerProfileImageUrl;
	private String content;
	private LocalDateTime createdAt;
	private List<CommentResponse> replies;
}