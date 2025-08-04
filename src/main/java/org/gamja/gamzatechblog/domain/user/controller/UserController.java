package org.gamja.gamzatechblog.domain.user.controller;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.user.controller.docs.UserControllerSwagger;
import org.gamja.gamzatechblog.domain.user.controller.response.UserActivityResponse;
import org.gamja.gamzatechblog.domain.user.controller.response.UserProfileResponse;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UpdateProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UserProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@ApiController("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserControllerSwagger {

	private final UserService userService;

	@GetMapping("/me/get/profile")
	public ResponseDto<UserProfileResponse> getCurrentUserProfile(@CurrentUser User user) {
		UserProfileResponse profile = userService.getCurrentUserProfile(user);
		return ResponseDto.of(HttpStatus.OK, "프로필 조회 성공", profile);
	}

	@PutMapping("/me/update/profile")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public ResponseDto<UserProfileResponse> updateProfile(@CurrentUser User user,
		@RequestBody UpdateProfileRequest profileRequest) {
		UserProfileResponse updated = userService.updateProfile(user, profileRequest);
		return ResponseDto.of(HttpStatus.OK, "프로필이 수정되었습니다", updated);
	}

	@DeleteMapping("/me/withdraw")
	public ResponseDto<String> withdraw(@CurrentUser User user) {
		userService.withdraw(user);
		return ResponseDto.of(HttpStatus.OK, "삭제되었습니다");
	}

	@PostMapping("/me/complete")
	@PreAuthorize("hasRole('PRE_REGISTER')")
	public ResponseDto<UserProfileResponse> completeProfile(
		@Valid @RequestBody UserProfileRequest userProfileRequest, @CurrentUser User currentUser) {
		UserProfileResponse updated = userService.setupUserProfile(currentUser.getGithubId(), userProfileRequest);
		return ResponseDto.of(HttpStatus.OK, "프로필이 성공적으로 완성되었습니다.", updated);
	}

	@GetMapping("/me/activity")
	public ResponseDto<UserActivityResponse> getActivitySummary(@CurrentUser User currentUser) {
		UserActivityResponse response = userService.getActivitySummary(currentUser);
		return ResponseDto.of(HttpStatus.OK, "유저 활동 정보 조회 성공", response);
	}

	@GetMapping("/me/role")
	@PreAuthorize("hasAnyRole('USER','PRE_REGISTER','ADMIN', 'PENDING')")
	public ResponseDto<String> getCurrentUserRole(@CurrentUser User currentUser) {
		String role = currentUser.getRole().name();
		return ResponseDto.of(HttpStatus.OK, "역할 조회 성공", role);
	}
}
