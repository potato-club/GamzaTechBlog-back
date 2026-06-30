package org.gamja.gamzatechblog.domain.profileimage.infrastructure;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.profileimage.service.port.ProfileImageRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProfileImageRepositoryImpl implements ProfileImageRepository {
	private final ProfileImageJpaRepository profileImageJpaRepository;

	@Override
	public ProfileImage saveProfileImage(ProfileImage profileImage) {
		return profileImageJpaRepository.save(profileImage);
	}

	@Override
	public Optional<ProfileImage> findByUser(User user) {
		return profileImageJpaRepository.findByUser(user);
	}

	@Override
	public void flush() {
		profileImageJpaRepository.flush();
	}
}
