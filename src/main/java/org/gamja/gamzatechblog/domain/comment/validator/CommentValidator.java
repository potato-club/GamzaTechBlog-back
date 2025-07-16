package org.gamja.gamzatechblog.domain.comment.validator;

import java.util.Optional;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.domain.comment.exception.CommentAccessDeniedException;
import org.gamja.gamzatechblog.domain.comment.exception.CommentNotFoundException;
import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.gamja.gamzatechblog.domain.comment.service.port.CommentRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommentValidator {

	private final CommentRepository commentRepository;

	public Comment validateCommentExists(Long commentId) {
		Optional<Comment> optionalComment = commentRepository.findCommentById(commentId);
		if (optionalComment.isPresent()) {
			return optionalComment.get();
		}
		throw new CommentNotFoundException(ErrorCode.COMMENT_NOT_FOUND);
	}

	public void validateCommentOwnership(Comment comment, User currentUser) {
		Long commentAuthorUserId = comment.getUser().getId();
		Long currentUserId = currentUser.getId();

		if (!commentAuthorUserId.equals(currentUserId)) {
			throw new CommentAccessDeniedException(ErrorCode.COMMENT_FORBIDDEN);
		}
	}

	public Comment resolveParent(Long parentCommentId) {
		return (parentCommentId == null) ? null : validateCommentExists(parentCommentId);
	}
}
