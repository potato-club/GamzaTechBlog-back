package org.gamja.gamzatechblog.domain.posttag.repository;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.posttag.model.entity.PostTag;
import org.gamja.gamzatechblog.domain.tag.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
	void deleteAllByTag(Tag tag);

	void deleteAllByPost(Post post);
}
