package org.gamja.gamzatechblog.domain.user.service;

import java.util.List;

import org.gamja.gamzatechblog.domain.user.controller.response.UserProfileResponse;

public interface AdminUserService {
	List<UserProfileResponse> getPendingUsers();

	void approveUserProfile(Long userId);
}
