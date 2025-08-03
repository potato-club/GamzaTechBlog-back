package org.gamja.gamzatechblog.domain.user.service.impl;

import java.util.List;

import org.gamja.gamzatechblog.domain.user.controller.response.PendingUserResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.model.mapper.UserProfileMapper;
import org.gamja.gamzatechblog.domain.user.model.type.UserRole;
import org.gamja.gamzatechblog.domain.user.service.AdminUserService;
import org.gamja.gamzatechblog.domain.user.service.port.UserRepository;
import org.gamja.gamzatechblog.domain.user.validator.AdminUserValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {

	private final UserRepository userRepository;
	private final UserProfileMapper userProfileMapper;
	private final AdminUserValidator adminUserValidator;

	@Override
	public List<PendingUserResponse> getPendingUsers() {
		List<User> pendings = userRepository.findAllByRole(UserRole.PENDING);
		return pendings.stream()
			.map(userProfileMapper::toPendingUserResponse)
			.toList();
	}

	@Override
	@Transactional
	public void approveUserProfile(Long userId) {
		User user = adminUserValidator.validateAndGetUser(userId);
		adminUserValidator.validatePending(user);
		user.setUserRole(UserRole.USER);
		userRepository.saveUser(user);
	}
}
