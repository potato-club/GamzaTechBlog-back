package org.gamja.gamzatechblog.domain.comment.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
	Long commentId,
	String writer,
	String writerProfileImageUrl,
	String content,
	LocalDateTime createdAt,
	List<CommentResponse> replies
) {
}
