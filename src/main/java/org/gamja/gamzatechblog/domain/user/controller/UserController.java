package org.gamja.gamzatechblog.domain.user.controller;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.domain.user.model.dto.UpdateProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.UserProfileDto;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.service.UserAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

	private final UserAuthService userAuthService;

	@Operation(summary = "정보 조회", tags = "유저 기능")
	@GetMapping("/me/get/profile")
	public ResponseEntity<ResponseDto<UserProfileDto>> getMyProfile(@CurrentUser User user) {
		UserProfileDto profile = userAuthService.getMyProfile(user);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "프로필 조회 성공", profile));
	}

	@Operation(summary = "정보 업데이트", tags = "유저 기능")
	@PutMapping("/me/update/profile")
	public ResponseEntity<ResponseDto<UserProfileDto>> updateProfile(@CurrentUser User user,
		@RequestBody UpdateProfileRequest profileRequest) {
		UserProfileDto updated = userAuthService.updateProfile(user, profileRequest);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "프로필이 수정되었습니다", updated));
	}

	@Operation(summary = "계정 삭제", tags = "유저 기능")
	@DeleteMapping("/me/withdraw")
	public ResponseEntity<ResponseDto<String>> withdraw(@CurrentUser User user) {
		userAuthService.withdraw(user);
		return ResponseEntity.ok(ResponseDto.of(HttpStatus.OK, "삭제되었습니다"));
	}
}
