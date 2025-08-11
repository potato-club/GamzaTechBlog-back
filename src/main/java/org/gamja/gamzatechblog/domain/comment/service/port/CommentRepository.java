package org.gamja.gamzatechblog.domain.comment.service.port;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepository {
	Optional<Comment> findCommentById(Long commentId);

	List<Comment> findAllByPostIdAndParentIsNullOrderByCreatedAtDesc(Long postId);

	int countByUser(User user);

	Page<Comment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

	Comment saveComment(Comment comment);

	void deleteComment(Comment comment);
}