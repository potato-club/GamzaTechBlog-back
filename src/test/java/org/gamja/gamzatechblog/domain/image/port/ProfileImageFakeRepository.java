package org.gamja.gamzatechblog.domain.image.port;

import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;

public class ProfileImageFakeRepository extends FakeBaseRepository<ProfileImage> {

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
}
