package org.gamja.gamzatechblog.domain.user.service;

import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.domain.user.controller.response.UserActivityResponse;
import org.gamja.gamzatechblog.domain.user.controller.response.UserProfileResponse;
import org.gamja.gamzatechblog.domain.user.controller.response.UserPublicProfileResponse;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UpdateProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UserProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Pageable;

public interface UserService {
	UserProfileResponse updateProfile(User currentUser, UpdateProfileRequest req);

	void withdraw(User currentUser);

	User getUserByGithubId(String githubId);

	boolean existsByGithubId(String githubId);

	User registerWithProvider(OAuthUserInfo info);

	void updateEmailIfEmpty(String githubId, String email);

	UserProfileResponse getCurrentUserProfile(User currentUser);

	UserProfileResponse setupUserProfile(String githubId, UserProfileRequest userProfileRequest);

	UserActivityResponse getActivitySummary(User user);

	UserPublicProfileResponse getPublicProfileByNickname(String nickname, Pageable pageable);
}
