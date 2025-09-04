package org.gamja.gamzatechblog.domain.introduction.infrastructure;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.introduction.model.entity.Introduction;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntroductionJpaRepository extends JpaRepository<Introduction, Long> {
	boolean existsByUser(User user);

	Optional<Introduction> findByUser(User user);

	Page<Introduction> findAll(Pageable pageable);
}
