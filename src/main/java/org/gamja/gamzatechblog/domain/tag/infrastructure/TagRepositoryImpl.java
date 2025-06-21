package org.gamja.gamzatechblog.domain.tag.infrastructure;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.tag.model.entity.Tag;
import org.gamja.gamzatechblog.domain.tag.service.port.TagRepository;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TagRepositoryImpl implements TagRepository {

	private final TagJpaRepository tagJpaRepository;

	/**
	 * Retrieves a tag entity by its name.
	 *
	 * @param tagName the name of the tag to search for
	 * @return an {@code Optional} containing the tag if found, or empty if not found
	 */
	@Override
	public Optional<Tag> findByTagName(String tagName) {
		return tagJpaRepository.findByTagName(tagName);
	}

	/**
	 * Retrieves a list of all tag names.
	 *
	 * @return a list containing the names of all tags
	 */
	@Override
	public List<String> findAllTagNames() {
		return tagJpaRepository.findAllTagNames();
	}

	/**
	 * Persists the given Tag entity to the database.
	 *
	 * @param tag the Tag entity to be saved
	 * @return the saved Tag entity
	 */
	@Override
	public Tag save(Tag tag) {
		return tagJpaRepository.save(tag);
	}

	/**
	 * Deletes the specified Tag entity from the data store.
	 *
	 * @param tag the Tag entity to be removed
	 */
	@Override
	public void delete(Tag tag) {
		tagJpaRepository.delete(tag);
	}

	/**
	 * Persists the given Tag entity and immediately flushes changes to the database.
	 *
	 * @param tag the Tag entity to be saved and flushed
	 * @return the persisted Tag entity
	 */
	@Override
	public Tag saveAndFlush(Tag tag) {
		return tagJpaRepository.saveAndFlush(tag);
	}

}
