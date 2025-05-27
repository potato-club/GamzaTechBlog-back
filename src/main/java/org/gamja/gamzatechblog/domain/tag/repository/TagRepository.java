package org.gamja.gamzatechblog.domain.tag.repository;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.tag.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
	Optional<Tag> findByTagName(String TagName);

	@Query("SELECT t.tagName FROM Tag t ORDER BY t.tagName")
	List<String> findAllTagNames();
}
