package org.gamja.gamzatechblog.domain.comment.service;

import java.util.List;

import org.gamja.gamzatechblog.domain.comment.model.dto.request.CommentRequest;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public interface CommentService {
	List<CommentResponse> getCommentsByPostId(Long postId);

	CommentResponse createComment(User currentUser, Long postId, CommentRequest commentRequest);

	void deleteComment(User currentUser, Long commentId);
}
