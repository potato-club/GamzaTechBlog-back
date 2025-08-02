package org.gamja.gamzatechblog.domain.user.controller;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.user.controller.response.UserProfileResponse;
import org.gamja.gamzatechblog.domain.user.service.AdminUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@ApiController("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

	private final AdminUserService adminService;

	@Operation(summary = "대기 중인(PENDING) 사용자 전체 조회", tags = "관리자 기능")
	@GetMapping("/pending")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseDto<List<UserProfileResponse>> getPendingUsers() {
		List<UserProfileResponse> pendings = adminService.getPendingUsers();
		return ResponseDto.of(HttpStatus.OK, "대기 중인 사용자 조회 성공", pendings);
	}

	@Operation(summary = "특정 사용자 승인 (PENDING → USER)", tags = "관리자 기능")
	@PutMapping("/{id}/approve")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseDto<Void> approveUserProfile(@PathVariable("id") Long userId) {
		adminService.approveUserProfile(userId);
		return ResponseDto.of(HttpStatus.OK, "사용자 승인 완료", null);
	}
}
