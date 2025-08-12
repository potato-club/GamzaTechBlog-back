package org.gamja.gamzatechblog.domain.comment.model.dto.request;

public record CommentRequest(
	String content,
	Long parentCommentId
) {
}
