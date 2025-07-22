package org.gamja.gamzatechblog.domain.profileimage.service;

import org.gamja.gamzatechblog.domain.profileimage.model.entity.ProfileImage;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageService {
	ProfileImage uploadProfileImage(MultipartFile file, User user);

	ProfileImage updateProfileImage(MultipartFile newFile, User user);

	ProfileImage getProfileImageByUser(User user);

	void deleteProfileImage(User user);

	ProfileImage uploadProfileImageFromUrl(String imageUrl, User user);
}
