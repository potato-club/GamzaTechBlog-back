package org.gamja.gamzatechblog.domain.comment.service.impl;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.comment.model.dto.CreateCommentCommand;
import org.gamja.gamzatechblog.domain.comment.model.dto.DeleteCommentCommand;
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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

	private static final String POST_DETAIL_CACHE = "postDetail";

	private final CommentRepository commentRepository;
	private final CommentMapper commentMapper;
	private final CommentValidator commentValidator;
	private final PostValidator postValidator;
	private final CacheManager cacheManager;

	@Override
	@Transactional(readOnly = true)
	public List<CommentResponse> getCommentsByPostId(Long postId) {
		postValidator.validatePostExists(postId);
		return commentRepository
			.findAllByPostIdAndParentIsNullOrderByCreatedAtDesc(postId)
			.stream()
			.map(commentMapper::mapToCommentTree)
			.toList();
	}

	@Override
	@Transactional
	public CommentResponse createComment(CreateCommentCommand createCommand) {
		Post post = postValidator.validatePostExists(createCommand.postId());
		Comment parent = commentValidator.resolveParent(createCommand.parentCommentId());
		commentValidator.validateParentBelongsToPost(createCommand.postId(), parent);

		Comment toSave = buildComment(post, createCommand.user(), parent, createCommand.content());
		Comment saved = commentRepository.saveComment(toSave);

		evictPostDetailCache(createCommand.postId());
		return commentMapper.mapToCommentTree(saved);
	}

	@Override
	@Transactional
	public void deleteComment(DeleteCommentCommand deleteCommand) {
		Comment existing = commentValidator.validateCommentExists(deleteCommand.commentId());
		commentValidator.validateCommentOwnership(existing, deleteCommand.currentUser());

		Long postId = existing.getPost().getId();
		commentRepository.deleteComment(existing);
		evictPostDetailCache(postId);
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

	private void evictPostDetailCache(Long postId) {
		Cache cache = cacheManager.getCache(POST_DETAIL_CACHE);
		if (cache != null) {
			cache.evict(postId);
		}
	}
}
