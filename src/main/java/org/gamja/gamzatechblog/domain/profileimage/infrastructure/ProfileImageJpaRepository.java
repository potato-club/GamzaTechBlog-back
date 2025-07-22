package org.gamja.gamzatechblog.domain.profileimage.infrastructure;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ProfileImageJpaRepository extends JpaRepository<ProfileImage, Long> {
	Optional<ProfileImage> findByUser(User user);

	@Transactional
	long deleteByUser(User user);

	void flush();
}
