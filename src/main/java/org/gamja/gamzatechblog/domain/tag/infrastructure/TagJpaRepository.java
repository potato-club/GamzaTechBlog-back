package org.gamja.gamzatechblog.domain.tag.infrastructure;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.tag.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TagJpaRepository extends JpaRepository<Tag, Long> {
	/**
 * Retrieves a Tag entity by its tag name.
 *
 * @param tagName the name of the tag to search for
 * @return an Optional containing the Tag if found, or empty if not found
 */
Optional<Tag> findByTagName(String tagName);

	/**
	 * Retrieves all tag names from the database, ordered alphabetically.
	 *
	 * @return a list of tag names sorted in ascending order
	 */
	@Query("SELECT t.tagName FROM Tag t ORDER BY t.tagName")
	List<String> findAllTagNames();
}
