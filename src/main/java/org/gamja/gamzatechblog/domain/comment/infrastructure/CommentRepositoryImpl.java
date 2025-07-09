package org.gamja.gamzatechblog.domain.comment.infrastructure;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.gamja.gamzatechblog.domain.comment.service.port.CommentRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CommentRepositoryImpl implements CommentRepository {
	private final CommentJpaRepository commentJpaRepository;

	@Override
	public Optional<Comment> findCommentById(Long commentId) {
		return commentJpaRepository.findById(commentId);
	}

	@Override
	public List<Comment> findAllByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId) {
		return commentJpaRepository.findAllByPostIdAndParentIsNullOrderByCreatedAtAsc(postId);
	}

	@Override
	public int countByUser(User user) {
		return commentJpaRepository.countByUser(user);
	}

	@Override
	public Page<Comment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
		return commentJpaRepository.findByUserOrderByCreatedAtDesc(user, pageable);
	}

	@Override
	public Comment saveComment(Comment comment) {
		return commentJpaRepository.save(comment);
	}

	@Override
	public void deleteComment(Comment comment) {
		commentJpaRepository.delete(comment);
	}
}
