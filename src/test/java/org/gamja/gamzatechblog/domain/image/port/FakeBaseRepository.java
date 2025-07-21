package org.gamja.gamzatechblog.domain.image.port;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class FakeBaseRepository<T> {

	protected final Map<Long, T> entityStore = new HashMap<>();

	private long sequence = 0L;

	protected T save(Function<Long, T> initializer, T entity, Long id) {
		if (id == null) {
			long newId = ++sequence;
			T saved = initializer.apply(newId);
			entityStore.put(newId, saved);
			return saved;
		}
		entityStore.put(id, entity);
		return entity;
	}

	public Optional<T> findById(Long id) {
		return Optional.ofNullable(entityStore.get(id));
	}

	public List<T> findAll() {
		return new ArrayList<>(entityStore.values());
	}

	public void deleteById(Long id) {
		entityStore.remove(id);
	}

	public void clear() {
		entityStore.clear();
	}
}