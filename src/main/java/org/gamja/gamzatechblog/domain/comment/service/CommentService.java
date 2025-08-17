package org.gamja.gamzatechblog.domain.comment.service;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.comment.model.dto.CreateCommentCommand;
import org.gamja.gamzatechblog.domain.comment.model.dto.DeleteCommentCommand;
import org.gamja.gamzatechblog.domain.comment.model.dto.request.CommentRequest;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentListResponse;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Pageable;

public interface CommentService {
	List<CommentResponse> getCommentsByPostId(Long postId);

	CommentResponse createComment(CreateCommentCommand createCommentCommand);

	void deleteComment(DeleteCommentCommand command);

	PagedResponse<CommentListResponse> getMyComments(User user, Pageable pageable);

	default CommentResponse createComment(User currentUser, Long postId, CommentRequest commentRequest) {
		return createComment(CreateCommentCommand.of(currentUser, postId, commentRequest));
	}

	default void deleteComment(User currentUser, Long commentId) {
		deleteComment(new DeleteCommentCommand(currentUser, commentId));
	}
}
