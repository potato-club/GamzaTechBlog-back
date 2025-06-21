package org.gamja.gamzatechblog.domain.user.service;

import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.domain.user.controller.response.UserActivityResponse;
import org.gamja.gamzatechblog.domain.user.controller.response.UserProfileResponse;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UpdateProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UserProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public interface UserService {
	UserProfileResponse updateProfile(User currentUser, UpdateProfileRequest req);

	void withdraw(User currentUser);

	User findByGithubId(String githubId);

	boolean existsByGithubId(String githubId);

	User registerWithProvider(OAuthUserInfo info);

	UserProfileResponse getMyProfile(User currentUser);

	UserProfileResponse completeProfile(String githubId, UserProfileRequest userProfileRequest);

	UserActivityResponse getUserActivity(User user);

}
