package org.gamja.gamzatechblog.domain.comment.service.port;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class CommentFakeRepository implements CommentRepository {

	private final AtomicLong seq = new AtomicLong(0);
	private final Map<Long, Comment> store = new LinkedHashMap<>();

	@Override
	public Optional<Comment> findCommentById(Long commentId) {
		return Optional.ofNullable(store.get(commentId));
	}

	@Override
	public List<Comment> findAllByPostIdAndParentIsNullOrderByCreatedAtDesc(Long postId) {
		return store.values().stream()
			.filter(c -> c.getPost() != null && c.getPost().getId().equals(postId))
			.filter(c -> c.getParent() == null)
			.sorted(Comparator.comparing(Comment::getId, Comparator.nullsLast(Long::compareTo)).reversed())
			.collect(Collectors.toList());
	}

	@Override
	public int countByUser(User user) {
		return (int)store.values().stream()
			.filter(c -> c.getUser() != null && c.getUser().getId().equals(user.getId()))
			.count();
	}

	@Override
	public Page<Comment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
		List<Comment> all = store.values().stream()
			.filter(c -> c.getUser() != null && c.getUser().getId().equals(user.getId()))
			.sorted(Comparator.comparing(Comment::getId, Comparator.nullsLast(Long::compareTo)).reversed())
			.toList();

		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), all.size());
		List<Comment> content = start >= end ? new ArrayList<>() : all.subList(start, end);

		return new PageImpl<>(content, pageable, all.size());
	}

	@Override
	public Comment saveComment(Comment comment) {
		if (comment.getId() == null) {
			try {
				var idField = comment.getClass().getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(comment, seq.incrementAndGet());
			} catch (Exception ignore) {
			}
		}
		store.put(comment.getId(), comment);
		return comment;
	}

	@Override
	public void deleteComment(Comment comment) {
		if (comment == null || comment.getId() == null)
			return;
		store.remove(comment.getId());
	}

	public void clear() {
		store.clear();
	}

	public Map<Long, Comment> internalStore() {
		return store;
	}
}
