package org.gamja.gamzatechblog.domain.user.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.domain.user.controller.response.UserActivityResponse;
import org.gamja.gamzatechblog.domain.user.controller.response.UserProfileResponse;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UpdateProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.dto.request.UserProfileRequest;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.model.type.Position;
import org.gamja.gamzatechblog.domain.user.model.type.UserRole;
import org.gamja.gamzatechblog.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

class UserControllerTest {

	@Mock
	private UserService userService;

	private UserController userController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		userController = new UserController(userService);
	}

	@Test
	@DisplayName("내 프로필 조회 성공 테스트")
	void getMyProfile_성공() {
		User mockUser = mock(User.class);
		UserProfileResponse mockProfile = new UserProfileResponse(
			"gh123",
			"parkjihun",
			"박지훈",
			"jihun@example.com",
			"http://img.url/me.png",
			"USER",
			9,
			"2025-06-26T10:00:00",
			"2025-06-26T12:00:00"
		);
		when(userService.getMyProfile(mockUser)).thenReturn(mockProfile);

		ResponseDto<UserProfileResponse> response = userController.getMyProfile(mockUser);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("프로필 조회 성공", response.getMessage());
		assertSame(mockProfile, response.getData());
		verify(userService).getMyProfile(mockUser);
	}

	@Test
	@DisplayName("프로필 업데이트 성공 테스트")
	void updateProfile_성공() {
		User mockUser = mock(User.class);

		UpdateProfileRequest req = new UpdateProfileRequest();
		req.setEmail("new@example.com");
		req.setStudentNumber("20201234");
		req.setGamjaBatch(10);
		req.setPosition(Position.BACKEND);

		UserProfileResponse updatedProfile = new UserProfileResponse(
			"gh123",
			"parkjihun",
			"박지훈",
			"new@example.com",
			"http://img.url/new.png",
			"USER",
			10,
			"2025-06-27T09:00:00",
			"2025-06-27T10:00:00"
		);
		when(userService.updateProfile(mockUser, req)).thenReturn(updatedProfile);

		ResponseDto<UserProfileResponse> response = userController.updateProfile(mockUser, req);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("프로필이 수정되었습니다", response.getMessage());
		assertSame(updatedProfile, response.getData());
		verify(userService).updateProfile(mockUser, req);
	}

	@Test
	@DisplayName("계정 삭제 성공 테스트")
	void withdraw_성공() {
		User mockUser = mock(User.class);

		ResponseDto<String> response = userController.withdraw(mockUser);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("삭제되었습니다", response.getMessage());
		assertNull(response.getData());
		verify(userService).withdraw(mockUser);
	}

	@Test
	@DisplayName("회원가입용 프로필 완료 테스트")
	void completeProfile_성공() {
		UserProfileRequest completeReq = new UserProfileRequest();
		completeReq.setEmail("first@example.com");
		completeReq.setStudentNumber("20201234");
		completeReq.setGamjaBatch(8);
		completeReq.setPosition(Position.BACKEND);

		User mockUser = mock(User.class);
		when(mockUser.getGithubId()).thenReturn("gh123");

		UserProfileResponse completed = new UserProfileResponse(
			"gh123",
			"parkjihun",
			"박지훈",
			"first@example.com",
			"http://img.url/first.png",
			"USER",
			8,
			"2025-06-27T08:00:00",
			"2025-06-27T09:00:00"
		);
		when(userService.completeProfile("gh123", completeReq)).thenReturn(completed);

		ResponseDto<UserProfileResponse> response =
			userController.completeProfile(completeReq, mockUser);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("프로필이 성공적으로 완성되었습니다.", response.getMessage());
		assertSame(completed, response.getData());
		verify(userService).completeProfile("gh123", completeReq);
	}

	@Test
	@DisplayName("유저 활동 정보 조회 성공 테스트")
	void getUserActivity_성공() {
		User mockUser = mock(User.class);
		UserActivityResponse activity = new UserActivityResponse(
			5,
			12,
			20
		);
		when(userService.getUserActivity(mockUser)).thenReturn(activity);

		ResponseDto<UserActivityResponse> response = userController.getUserActivity(mockUser);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("유저 활동 정보 조회 성공", response.getMessage());
		assertSame(activity, response.getData());
		verify(userService).getUserActivity(mockUser);
	}

	@Test
	@DisplayName("유저 역할 조회 성공 테스트")
	void getMyRole_성공() {
		User mockUser = mock(User.class);
		when(mockUser.getRole()).thenReturn(UserRole.USER);

		ResponseDto<String> response = userController.getMyRole(mockUser);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("역할 조회 성공", response.getMessage());
		assertEquals("USER", response.getData());
		verify(mockUser).getRole();
	}
}
