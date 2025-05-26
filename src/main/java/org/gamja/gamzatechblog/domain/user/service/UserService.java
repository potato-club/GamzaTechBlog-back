package org.gamja.gamzatechblog.domain.user.service;

import org.gamja.gamzatechblog.domain.user.model.dto.UpdateProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.UserProfileResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public interface UserService {
	UserProfileResponse updateProfile(User currentUser, UpdateProfileRequest req);

	void withdraw(User currentUser);

	UserProfileResponse getMyProfile(User currentUser);
}
