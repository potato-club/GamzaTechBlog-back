package org.gamja.gamzatechblog.domain.tag.service.port;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.tag.model.entity.Tag;

public interface TagRepository {
	Optional<Tag> findByTagName(String tagName);

	List<String> findAllTagNames();

	Tag save(Tag tag);

	void deleteOrphanTags();
}
