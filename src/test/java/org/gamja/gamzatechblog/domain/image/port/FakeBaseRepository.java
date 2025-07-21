package org.gamja.gamzatechblog.domain.image.port;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class FakeBaseRepository<T> {

	protected final Map<Long, T> imageStore = new HashMap<>();

	private long sequence = 0L;

	protected T save(Function<Long, T> initializer, T entity, Long id) {
		if (id == null) {
			long newId = ++sequence;
			T saved = initializer.apply(newId);
			imageStore.put(newId, saved);
			return saved;
		}
		imageStore.put(id, entity);
		return entity;
	}

	public Optional<T> findById(Long id) {
		return Optional.ofNullable(imageStore.get(id));
	}

	public List<T> findAll() {
		return new ArrayList<>(imageStore.values());
	}

	public void deleteById(Long id) {
		imageStore.remove(id);
	}

	public void clear() {
		imageStore.clear();
	}
}