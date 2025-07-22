package org.gamja.gamzatechblog.domain.profileimage.infrastructure;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageJpaRepository extends JpaRepository<ProfileImage, Long> {
	Optional<ProfileImage> findByUser(User user);

	long deleteByUser(User user);

	void flush();
}
