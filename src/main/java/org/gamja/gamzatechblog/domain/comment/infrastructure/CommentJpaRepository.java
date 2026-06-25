package org.gamja.gamzatechblog.domain.comment.infrastructure;

import java.util.List;

import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {

	// 최상위 댓글 + 작성자·프로필이미지를 한 번에 로드(N+1 방지). 대댓글의 작성자는 batch_fetch_size로 일괄 로드
	@Query("select distinct c from Comment c "
		+ "join fetch c.user u "
		+ "left join fetch u.profileImage "
		+ "where c.post.id = :postId and c.parent is null "
		+ "order by c.createdAt desc")
	List<Comment> findAllByPostIdAndParentIsNullOrderByCreatedAtDesc(@Param("postId") Long postId);

	int countByUser(User user);

	Page<Comment> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
