package org.gamja.gamzatechblog.domain.user.controller;

import static org.gamja.gamzatechblog.support.user.UserFixtures.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.domain.user.controller.response.UserActivityResponse;
import org.gamja.gamzatechblog.domain.user.controller.response.UserProfileResponse;
import org.gamja.gamzatechblog.domain.user.exception.UserNotFoundException;
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
		User fixture = user("gh123");
		UserProfileResponse mockProfile = new UserProfileResponse(
			fixture.getGithubId(), fixture.getNickname(), fixture.getName(), fixture.getEmail(),
			"http://img.url/me.png", fixture.getRole().name(), fixture.getGamjaBatch(),
			"2025-06-26T10:00:00", "2025-06-26T12:00:00"
		);
		when(userService.getMyProfile(fixture)).thenReturn(mockProfile);

		ResponseDto<UserProfileResponse> response = userController.getMyProfile(fixture);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("프로필 조회 성공", response.getMessage());
		assertSame(mockProfile, response.getData());
		verify(userService).getMyProfile(fixture);
	}

	@Test
	@DisplayName("내 프로필 조회 실패 - 사용자 없음")
	void getMyProfile_실패_사용자없음() {
		User fixture = user("gh456");
		when(userService.getMyProfile(fixture))
			.thenThrow(new UserNotFoundException(1L));

		assertThrows(
			UserNotFoundException.class,
			() -> userController.getMyProfile(fixture)
		);
	}

	@Test
	@DisplayName("프로필 업데이트 성공 테스트")
	void updateProfile_성공() {
		User fixture = user("gh123");
		UpdateProfileRequest req = new UpdateProfileRequest(
			"new@example.com",
			"20201234",
			10,
			Position.BACKEND
		);

		UserProfileResponse updatedProfile = new UserProfileResponse(
			fixture.getGithubId(), fixture.getNickname(), fixture.getName(), req.getEmail(),
			"http://img.url/new.png", fixture.getRole().name(), req.getGamjaBatch(),
			"2025-06-27T09:00:00", "2025-06-27T10:00:00"
		);
		when(userService.updateProfile(fixture, req)).thenReturn(updatedProfile);

		ResponseDto<UserProfileResponse> response = userController.updateProfile(fixture, req);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("프로필이 수정되었습니다", response.getMessage());
		assertSame(updatedProfile, response.getData());
		verify(userService).updateProfile(fixture, req);
	}

	@Test
	@DisplayName("프로필 업데이트 실패 - 잘못된 데이터")
	void updateProfile_실패() {
		User fixture = user("gh123");
		UpdateProfileRequest req = new UpdateProfileRequest(null, null, null, null);
		when(userService.updateProfile(fixture, req))
			.thenThrow(new IllegalArgumentException("유효하지 않은 요청"));

		IllegalArgumentException ex = assertThrows(
			IllegalArgumentException.class,
			() -> userController.updateProfile(fixture, req)
		);
		assertEquals("유효하지 않은 요청", ex.getMessage());
	}

	@Test
	@DisplayName("계정 삭제 성공 테스트")
	void withdraw_성공() {
		User fixture = user("gh123");

		ResponseDto<String> response = userController.withdraw(fixture);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("삭제되었습니다", response.getMessage());
		assertNull(response.getData());
		verify(userService).withdraw(fixture);
	}

	@Test
	@DisplayName("계정 삭제 실패 - 서비스 오류")
	void withdraw_실패_서비스오류() {
		User fixture = user("gh123");
		doThrow(new RuntimeException("삭제 처리 중 오류")).when(userService).withdraw(fixture);

		assertThrows(RuntimeException.class, () -> userController.withdraw(fixture));
	}

	@Test
	@DisplayName("회원가입용 프로필 완료 성공 테스트")
	void completeProfile_성공() {
		UserProfileRequest completeReq = new UserProfileRequest(
			"first@example.com",
			"20201234",
			8,
			Position.BACKEND
		);

		User fixture = user("gh123");

		UserProfileResponse completed = new UserProfileResponse(
			fixture.getGithubId(), fixture.getNickname(), fixture.getName(), completeReq.getEmail(),
			"http://img.url/first.png", fixture.getRole().name(), completeReq.getGamjaBatch(),
			"2025-06-27T08:00:00", "2025-06-27T09:00:00"
		);
		when(userService.completeProfile(fixture.getGithubId(), completeReq)).thenReturn(completed);

		ResponseDto<UserProfileResponse> response =
			userController.completeProfile(completeReq, fixture);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("프로필이 성공적으로 완성되었습니다.", response.getMessage());
		assertSame(completed, response.getData());
		verify(userService).completeProfile(fixture.getGithubId(), completeReq);
	}

	@Test
	@DisplayName("프로필 완료 실패 - 이미 완료됨")
	void completeProfile_실패() {
		UserProfileRequest req = new UserProfileRequest(
			"a@b.com",
			"20201234",
			8,
			Position.BACKEND
		);

		User fixture = user("gh123");
		when(userService.completeProfile(fixture.getGithubId(), req))
			.thenThrow(new IllegalStateException("이미 프로필이 완성되었습니다."));

		IllegalStateException ex = assertThrows(
			IllegalStateException.class,
			() -> userController.completeProfile(req, fixture)
		);
		assertEquals("이미 프로필이 완성되었습니다.", ex.getMessage());
	}

	@Test
	@DisplayName("유저 활동 정보 조회 성공 테스트")
	void getUserActivity_성공() {
		User fixture = user("gh123");
		UserActivityResponse activity = new UserActivityResponse(5, 12, 20);
		when(userService.getUserActivity(fixture)).thenReturn(activity);

		ResponseDto<UserActivityResponse> response = userController.getUserActivity(fixture);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("유저 활동 정보 조회 성공", response.getMessage());
		assertSame(activity, response.getData());
		verify(userService).getUserActivity(fixture);
	}

	@Test
	@DisplayName("유저 역할 조회 성공 테스트")
	void getMyRole_성공() {
		User real = user("gh123");
		User fixture = spy(real);
		when(fixture.getRole()).thenReturn(UserRole.USER);

		ResponseDto<String> response = userController.getMyRole(fixture);

		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("역할 조회 성공", response.getMessage());
		assertEquals("USER", response.getData());
		verify(fixture).getRole();
	}

}
