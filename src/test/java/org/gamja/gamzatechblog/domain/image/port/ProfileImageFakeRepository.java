package org.gamja.gamzatechblog.domain.image.port;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	public Optional<ProfileImage> findProfileImageById(Long id) {
		return super.findById(id);
	}

	@Override
	public void deleteProfileImageById(Long id) {
		super.deleteById(id);
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

	@Override
	public void delete(ProfileImage entity) {
		if (entity != null && entity.getId() != null) {
			super.deleteById(entity.getId());
		}
	}

	@Override
	public long deleteByUser(User user) {
		if (user == null || user.getId() == null)
			return 0L;

		List<Long> ids = findAll().stream()
			.filter(pi -> pi.getUser() != null && user.getId().equals(pi.getUser().getId()))
			.map(ProfileImage::getId)
			.collect(Collectors.toList());

		ids.forEach(super::deleteById);
		return ids.size();
	}
}
