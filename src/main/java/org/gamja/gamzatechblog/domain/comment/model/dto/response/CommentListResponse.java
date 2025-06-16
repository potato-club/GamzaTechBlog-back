package org.gamja.gamzatechblog.domain.comment.model.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentListResponse {
	private Long commentId;
	private String content;
	private LocalDateTime createdAt;
	private Long postId;
	private String postTitle;
}
