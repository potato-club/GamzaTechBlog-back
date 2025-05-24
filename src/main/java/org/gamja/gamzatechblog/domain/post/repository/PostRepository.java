package org.gamja.gamzatechblog.domain.post.repository;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}