package org.gamja.gamzatechblog.domain.tag.service.port;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.tag.model.entity.Tag;

public class TagFakeRepository implements TagRepository {

	private final Map<String, Tag> inMemoryTagStore = new LinkedHashMap<>();

	@Override
	public Optional<Tag> findByTagName(String tagName) {
		return Optional.ofNullable(inMemoryTagStore.get(tagName));
	}

	@Override
	public List<String> findAllTagNames() {
		return new ArrayList<>(inMemoryTagStore.keySet());
	}

	@Override
	public Tag save(Tag tag) {
		inMemoryTagStore.put(tag.getTagName(), tag);
		return tag;
	}

	@Override
	public void delete(Tag tag) {
		inMemoryTagStore.remove(tag.getTagName());
	}

	@Override
	public Tag saveAndFlush(Tag tag) {
		return save(tag);
	}

	@Override
	public void deleteOrphanTags() {
	}

	public void clearStore() {
		inMemoryTagStore.clear();
	}

	public void deleteOrphanTagsByInUseNames(Iterable<String> inUseTagNames) {
		java.util.Set<String> inUse = new java.util.HashSet<>();
		for (String name : inUseTagNames)
			inUse.add(name);

		inMemoryTagStore.entrySet().removeIf(e -> !inUse.contains(e.getKey()));
	}
}
