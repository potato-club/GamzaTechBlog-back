package org.gamja.gamzatechblog.domain.user.controller;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UpdateProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UserProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.response.UserProfileResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@ApiController("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@Operation(summary = "정보 조회", tags = "유저 기능")
	@GetMapping("/me/get/profile")
	public ResponseEntity<ResponseDto<UserProfileResponse>> getMyProfile(@CurrentUser User user) {
		UserProfileResponse profile = userService.getMyProfile(user);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "프로필 조회 성공", profile));
	}

	@Operation(summary = "정보 업데이트(마이페이지용)", tags = "유저 기능")
	@PutMapping("/me/update/profile")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ResponseDto<UserProfileResponse>> updateProfile(@CurrentUser User user,
		@RequestBody UpdateProfileRequest profileRequest) {
		UserProfileResponse updated = userService.updateProfile(user, profileRequest);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "프로필이 수정되었습니다", updated));
	}

	@Operation(summary = "계정 삭제", tags = "유저 기능")
	@DeleteMapping("/me/withdraw")
	public ResponseEntity<ResponseDto<String>> withdraw(@CurrentUser User user) {
		userService.withdraw(user);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "삭제되었습니다"));
	}

	@Operation(summary = "추가 정보 입력(회원가입용)", tags = "유저 기능")
	@PostMapping("/me/complete")
	@PreAuthorize("hasRole('PRE_REGISTER')")
	public ResponseEntity<ResponseDto<UserProfileResponse>> completeProfile(
		@Valid @RequestBody UserProfileRequest userProfileRequest, @CurrentUser User currentUser) {
		UserProfileResponse updated = userService.completeProfile(currentUser.getGithubId(), userProfileRequest);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "프로필이 성공적으로 완성되었습니다.", updated));
	}
}
