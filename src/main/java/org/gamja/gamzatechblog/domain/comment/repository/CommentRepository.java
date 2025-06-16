package org.gamja.gamzatechblog.domain.comment.repository;

import java.util.List;

import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findAllByPostIdAndParentIsNullOrderByCreatedAtAsc(Long postId);

	int countByUser(User user);

}