package org.gamja.gamzatechblog.domain.image.port;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.profileimage.service.port.ProfileImageRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public class ProfileImageFakeRepository extends FakeBaseRepository<ProfileImage> implements ProfileImageRepository {

	public ProfileImage save(ProfileImage entity) {
		return super.save(id ->
				ProfileImage.builder()
					.id(id)
					.user(entity.getUser())
					.profileImageUrl(entity.getProfileImageUrl())
					.build(),
			entity,
			entity.getId());
	}

	@Override
	public ProfileImage saveProfileImage(ProfileImage entity) {
		return save(entity);
	}

	@Override
	public Optional<ProfileImage> findByUser(User user) {
		return findAll().stream()
			.filter(pi -> pi.getUser() != null && user != null && user.getId() != null
				&& user.getId().equals(pi.getUser().getId()))
			.findFirst();
	}

	@Override
	public void flush() {
	}

	@Override
	public Optional<ProfileImage> findById(Long id) {
		return super.findById(id);
	}

	@Override
	public List<ProfileImage> findAll() {
		return super.findAll();
	}
}
