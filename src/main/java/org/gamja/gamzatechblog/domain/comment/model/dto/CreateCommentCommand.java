package org.gamja.gamzatechblog.domain.comment.model.dto;

import org.gamja.gamzatechblog.domain.comment.model.dto.request.CommentRequest;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public record CreateCommentCommand(
	User user,
	Long postId,
	String content,
	Long parentCommentId
) {
	public static CreateCommentCommand of(User user, Long postId, CommentRequest req) {
		return new CreateCommentCommand(user, postId, req.content(), req.parentCommentId());
	}
}
