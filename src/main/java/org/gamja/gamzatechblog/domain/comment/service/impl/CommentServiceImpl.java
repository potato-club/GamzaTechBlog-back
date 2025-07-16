package org.gamja.gamzatechblog.domain.comment.service.impl;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.comment.model.dto.request.CommentRequest;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentListResponse;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentResponse;
import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.gamja.gamzatechblog.domain.comment.model.mapper.CommentMapper;
import org.gamja.gamzatechblog.domain.comment.service.CommentService;
import org.gamja.gamzatechblog.domain.comment.service.port.CommentRepository;
import org.gamja.gamzatechblog.domain.comment.validator.CommentValidator;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
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
			.map(commentMapper::mapToCommentTree)
			.toList();
	}

	@Override
	public CommentResponse createComment(User user, Long postId, CommentRequest req) {
		Post post = postValidator.validatePostExists(postId);
		Comment parent = commentValidator.resolveParent(req.getParentCommentId());
		Comment comment = buildComment(post, user, parent, req.getContent());
		Comment saved = commentRepository.saveComment(comment);
		return commentMapper.mapToCommentTree(saved);
	}

	@Override
	public void deleteComment(User currentUser, Long commentId) {
		Comment existing = commentValidator.validateCommentExists(commentId);
		commentValidator.validateCommentOwnership(existing, currentUser);
		commentRepository.deleteComment(existing);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<CommentListResponse> getMyComments(User user, Pageable pageable) {
		Page<Comment> page = commentRepository.findByUserOrderByCreatedAtDesc(user, pageable);
		return PagedResponse.of(page, commentMapper::toCommentListResponse);
	}

	private Comment buildComment(Post post, User user, Comment parent, String content) {
		return Comment.builder()
			.post(post)
			.user(user)
			.parent(parent)
			.content(content)
			.build();
	}
}
