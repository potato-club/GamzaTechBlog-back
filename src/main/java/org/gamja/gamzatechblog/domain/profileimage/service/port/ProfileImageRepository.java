package org.gamja.gamzatechblog.domain.profileimage.service.port;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;

public interface ProfileImageRepository {
	ProfileImage saveProfileImage(ProfileImage profileImage);

	Optional<ProfileImage> findProfileImageById(Long id);

	void deleteProfileImageById(Long id);
}
