package org.gamja.gamzatechblog.domain.comment.model.dto;

import org.gamja.gamzatechblog.domain.user.model.entity.User;

public record DeleteCommentCommand(
	User currentUser,
	Long commentId
) {
}
