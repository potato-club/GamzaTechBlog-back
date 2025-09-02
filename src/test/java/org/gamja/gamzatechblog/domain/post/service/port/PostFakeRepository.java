package org.gamja.gamzatechblog.domain.post.service.port;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class PostFakeRepository implements PostRepository {

	private final AtomicLong seq = new AtomicLong(0);
	private final Map<Long, Post> store = new LinkedHashMap<>();

	@Override
	public Optional<Post> findById(Long postId) {
		return Optional.ofNullable(store.get(postId));
	}

	@Override
	public Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
		var all = store.values().stream()
			.filter(p -> p.getUser() != null && p.getUser().getId().equals(user.getId()))
			.sorted(Comparator.comparing(Post::getId, Comparator.nullsLast(Long::compareTo)).reversed())
			.toList();

		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), all.size());
		var content = start >= end ? java.util.List.<Post>of() : all.subList(start, end);

		return new PageImpl<>(content, pageable, all.size());
	}

	@Override
	public int countByUser(User user) {
		return (int)store.values().stream()
			.filter(p -> p.getUser() != null && p.getUser().getId().equals(user.getId()))
			.count();
	}

	@Override
	public Post save(Post post) {
		if (post.getId() == null) {
			try {
				var f = post.getClass().getDeclaredField("id");
				f.setAccessible(true);
				f.set(post, seq.incrementAndGet());
			} catch (Exception ignore) {
			}
		}
		store.put(post.getId(), post);
		return post;
	}

	@Override
	public void delete(Post post) {
		if (post != null && post.getId() != null) {
			store.remove(post.getId());
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void deleteAllByUser(User user) {
		store.entrySet().removeIf(e ->
			e.getValue().getUser() != null &&
				e.getValue().getUser().getId().equals(user.getId())
		);
	}

	// 테스트 편의
	public void clear() {
		store.clear();
	}

	public Map<Long, Post> internalStore() {
		return store;
	}
}
