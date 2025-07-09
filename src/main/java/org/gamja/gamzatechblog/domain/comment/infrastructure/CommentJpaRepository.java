package org.gamja.gamzatechblog.domain.comment.infrastructure;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

	Optional<Comment> findById(Long id);

	List<Comment> findAllByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId);

	int countByUser(User user);

	Page<Comment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
