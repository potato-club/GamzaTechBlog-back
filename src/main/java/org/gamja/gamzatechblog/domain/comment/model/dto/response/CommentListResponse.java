package org.gamja.gamzatechblog.domain.comment.model.dto.response;

import java.time.LocalDateTime;

public record CommentListResponse(
	Long commentId,
	String content,
	LocalDateTime createdAt,
	Long postId,
	String postTitle
) {
}
