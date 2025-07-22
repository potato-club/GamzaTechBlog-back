package org.gamja.gamzatechblog.domain.profileimage.infrastructure;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.profileimage.service.port.ProfileImageRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProfileImageRepositoryImpl implements ProfileImageRepository {
	private final ProfileImageJpaRepository profileImageJpaRepository;

	public ProfileImageRepositoryImpl(ProfileImageJpaRepository profileImageJpaRepository) {
		this.profileImageJpaRepository = profileImageJpaRepository;
	}

	@Override
	public ProfileImage saveProfileImage(ProfileImage profileImage) {
		return profileImageJpaRepository.save(profileImage);
	}

	@Override
	public Optional<ProfileImage> findProfileImageById(Long id) {
		return profileImageJpaRepository.findById(id);
	}

	@Override
	public void deleteProfileImageById(Long id) {
		profileImageJpaRepository.deleteById(id);
	}
}
