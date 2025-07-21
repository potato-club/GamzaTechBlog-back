package org.gamja.gamzatechblog.domain.profileimage.infrastructure;

import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageJpaRepository extends JpaRepository<ProfileImage, Long> {
}
