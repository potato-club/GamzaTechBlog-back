package org.gamja.gamzatechblog.domain.user.service;

import org.gamja.gamzatechblog.domain.user.model.dto.UpdateProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.UserProfileDto;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public interface UserService {
	UserProfileDto updateProfile(User currentUser, UpdateProfileRequest req);

	void withdraw(User currentUser);

	UserProfileDto getMyProfile(User currentUser);
}
