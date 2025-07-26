package org.gamja.gamzatechblog.domain.tag.infrastructure;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.tag.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TagJpaRepository extends JpaRepository<Tag, Long> {
	Optional<Tag> findByTagName(String tagName);

	@Query("SELECT t.tagName FROM Tag t ORDER BY t.tagName")
	List<String> findAllTagNames();

	@Modifying
	@Transactional
	@Query("DELETE FROM Tag t " + " WHERE NOT EXISTS (" + "   SELECT pt FROM PostTag pt WHERE pt.tag = t" + ")")
	void deleteOrphanTags();
}
