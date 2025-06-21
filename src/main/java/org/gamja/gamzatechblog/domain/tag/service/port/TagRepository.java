package org.gamja.gamzatechblog.domain.tag.service.port;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.tag.model.entity.Tag;

public interface TagRepository {
	/**
 * Retrieves a tag entity by its name.
 *
 * @param tagName the name of the tag to search for
 * @return an Optional containing the found Tag, or empty if no tag with the given name exists
 */
Optional<Tag> findByTagName(String tagName);

	/**
 * Retrieves a list of all tag names.
 *
 * @return a list containing the names of all tags
 */
List<String> findAllTagNames();

	/**
 * Persists the given Tag entity and returns the saved instance.
 *
 * @param tag the Tag entity to be saved
 * @return the saved Tag entity
 */
Tag save(Tag tag);

	/**
 * Removes the specified Tag entity from the data store.
 *
 * @param tag the Tag entity to be deleted
 */
void delete(Tag tag);

	/**
 * Saves the given Tag entity and immediately flushes changes to the underlying storage.
 *
 * @param tag the Tag entity to be saved
 * @return the persisted Tag entity
 */
Tag saveAndFlush(Tag tag);
}
