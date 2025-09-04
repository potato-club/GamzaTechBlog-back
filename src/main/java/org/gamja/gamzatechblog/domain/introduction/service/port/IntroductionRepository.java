package org.gamja.gamzatechblog.domain.introduction.service.port;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.introduction.model.entity.Introduction;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IntroductionRepository {
	boolean existsByUser(User user);

	Optional<Introduction> findByUser(User user);

	Optional<Introduction> findById(Long introId);

	Introduction save(Introduction intro);

	void delete(Introduction intro);

	Page<Introduction> findAll(Pageable pageable);
}
