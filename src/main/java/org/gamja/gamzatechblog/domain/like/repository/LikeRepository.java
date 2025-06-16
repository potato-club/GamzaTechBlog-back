package org.gamja.gamzatechblog.domain.like.repository;

import java.util.List;

import org.gamja.gamzatechblog.domain.like.model.entity.Like;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
	List<Like> findByUserOrderByCreatedAtDesc(User user);
}
