package org.gamja.gamzatechblog.domain.introduction;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroductionJpaRepository extends JpaRepository<Introduction, Long> {
	boolean existsByUser(User user);

	Optional<Introduction> findByUser(User user);

	Optional<Introduction> findByUser_Id(Long userId);

	Page<Introduction> findAll(Pageable pageable);

	default List<Introduction> findAllOrderByCreatedDesc() {
		return findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
	}

}
