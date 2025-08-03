package org.gamja.gamzatechblog.domain.user.service;

import java.util.List;

import org.gamja.gamzatechblog.domain.user.controller.response.PendingUserResponse;

public interface AdminUserService {
	List<PendingUserResponse> getPendingUsers();

	void approveUserProfile(Long userId);
}
