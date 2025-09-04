package org.gamja.gamzatechblog.domain.introduction;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IntroductionRepository {
	boolean existsByUser(User user);

	Optional<Introduction> findByUser(User user);

	Optional<Introduction> findById(Long introId);

	Optional<Introduction> findByUserId(Long userId);

	List<Introduction> findAllOrderByCreatedDesc();

	Introduction save(Introduction intro);

	void delete(Introduction intro);

	Page<Introduction> findAll(Pageable pageable);
}
