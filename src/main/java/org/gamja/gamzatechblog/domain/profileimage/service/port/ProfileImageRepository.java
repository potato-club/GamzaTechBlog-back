package org.gamja.gamzatechblog.domain.profileimage.service.port;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public interface ProfileImageRepository {
	ProfileImage saveProfileImage(ProfileImage profileImage);

	Optional<ProfileImage> findByUser(User user);

	void flush();
}
