package org.gamja.gamzatechblog.domain.comment.service.impl;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.comment.model.dto.request.CommentRequest;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentListResponse;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentResponse;
import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.gamja.gamzatechblog.domain.comment.model.mapper.CommentMapper;
import org.gamja.gamzatechblog.domain.comment.repository.CommentRepository;
import org.gamja.gamzatechblog.domain.comment.service.CommentService;
import org.gamja.gamzatechblog.domain.comment.validator.CommentValidator;
import org.gamja.gamzatechblog.domain.post.validator.PostValidator;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final CommentMapper commentMapper;
	private final CommentValidator commentValidator;
	private final PostValidator postValidator;

	@Override
	@Transactional(readOnly = true)
	public List<CommentResponse> getCommentsByPostId(Long postId) {
		postValidator.validatePostExists(postId);
		return commentRepository
			.findAllByPostIdAndParentIsNullOrderByCreatedAtAsc(postId)
			.stream()
			.map(commentMapper::toResponse)
			.toList();
	}

	@Override
	public CommentResponse createComment(User currentUser,
		Long postId,
		CommentRequest commentRequest) {
		var post = postValidator.validatePostExists(postId);

		Comment parent = null;
		if (commentRequest.getParentCommentId() != null) {
			parent = commentValidator.validateCommentExists(commentRequest.getParentCommentId());
		}
		Comment comment = Comment.builder()
			.post(post)
			.user(currentUser)
			.parent(parent)
			.content(commentRequest.getContent())
			.build();

		Comment saved = commentRepository.save(comment);
		return commentMapper.toResponse(saved);
	}

	@Override
	public void deleteComment(User currentUser, Long commentId) {
		Comment existing = commentValidator.validateCommentExists(commentId);
		commentValidator.validateCommentOwnership(existing, currentUser);
		commentRepository.delete(existing);
	}

	//코드 수정 예정
	@Override
	public PagedResponse<CommentListResponse> getMyComments(User user, Pageable pageable) {
		Page<Comment> comments = commentRepository.findByUserOrderByCreatedAtDesc(user, pageable);
		List<CommentListResponse> content = comments.getContent().stream()
			.map(commentMapper::toCommentListResponse)
			.toList();
		return new PagedResponse<>(content, comments.getNumber(), comments.getSize(), comments.getTotalElements(),
			comments.getTotalPages());
	}
}
