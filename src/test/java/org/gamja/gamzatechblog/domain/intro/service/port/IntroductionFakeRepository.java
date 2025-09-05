package org.gamja.gamzatechblog.domain.intro.service.port;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.gamja.gamzatechblog.domain.introduction.model.entity.Introduction;
import org.gamja.gamzatechblog.domain.introduction.service.port.IntroductionRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class IntroductionFakeRepository implements IntroductionRepository {

	private final Map<Long, Introduction> store = new LinkedHashMap<>();
	private final AtomicLong seq = new AtomicLong(1L);

	@Override
	public boolean existsByUser(User user) {
		return store.values().stream()
			.anyMatch(intro -> intro.getUser().equals(user));
	}

	@Override
	public Optional<Introduction> findByUser(User user) {
		return store.values().stream()
			.filter(intro -> intro.getUser().equals(user))
			.findFirst();
	}

	@Override
	public Optional<Introduction> findById(Long introId) {
		return Optional.ofNullable(store.get(introId));
	}

	// 새 엔티티라면 id를 새로 부여
	@Override
	public Introduction save(Introduction intro) {
		if (intro.getId() == null) {
			try {
				var idField = Introduction.class.getDeclaredField("id");
				idField.setAccessible(true);
				idField.set(intro, seq.getAndIncrement());
			} catch (Exception ignored) {
			}
		}
		store.put(intro.getId(), intro);
		return intro;
	}

	@Override
	public void delete(Introduction intro) {
		store.remove(intro.getId());
	}

	@Override
	public Page<Introduction> findAll(Pageable pageable) {
		List<Introduction> all = new ArrayList<>(store.values());
		int start = (int)Math.min(pageable.getOffset(), all.size());
		int end = Math.min(start + pageable.getPageSize(), all.size());
		List<Introduction> content = all.subList(start, end);
		return new PageImpl<>(content, pageable, all.size());
	}
}
