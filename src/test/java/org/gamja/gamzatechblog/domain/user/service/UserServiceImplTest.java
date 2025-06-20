package org.gamja.gamzatechblog.domain.user.service;

import static org.assertj.core.api.Assertions.*;

import org.gamja.gamzatechblog.domain.user.exception.UserNotFoundException;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.model.mapper.UserMapper;
import org.gamja.gamzatechblog.domain.user.model.mapper.UserProfileMapperImpl;
import org.gamja.gamzatechblog.domain.user.service.impl.UserServiceImpl;
import org.gamja.gamzatechblog.domain.user.service.port.UserFakeUserRepository;
import org.gamja.gamzatechblog.domain.user.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserServiceImpl메서드 단위 테스트")
public class UserServiceImplTest {

	private UserServiceImpl userService;
	private UserFakeUserRepository repository;

	@BeforeEach
	void init() {
		repository = new UserFakeUserRepository();
		UserMapper userMapper = new UserMapper();
		UserValidator userValidator = new UserValidator(repository);
		UserProfileMapperImpl profileMapper = new UserProfileMapperImpl();

		userService = new UserServiceImpl(
			repository,
			userMapper,
			userValidator,
			profileMapper,
			null,
			null,
			null
		);
	}

	@Test
	@DisplayName("existsByGithubId: 저장된 ID일 때 true, 미저장 ID일 때 false 반환")
	void existsByGithubId_behavior() {
		// Given
		User user = User.builder()
			.githubId("git2")
			.name("User2")
			.email("u2@mail.com")
			.studentNumber("SN2")
			.gamjaBatch(1)
			.nickname("nick2")
			.position(null)
			.build();
		repository.save(user);

		// When
		boolean known = userService.existsByGithubId("git2");
		boolean unknown = userService.existsByGithubId("unknown");

		// Then
		assertThat(known).isTrue();
		assertThat(unknown).isFalse();
	}

	@Test
	@DisplayName("findByGithubId: 존재하지 않는 ID 조회 시 UserNotFoundException 발생")
	void findByGithubId_notFound() {
		// Given: 없는 ID
		String missingId = "none";

		// When & Then
		assertThatThrownBy(() -> userService.findByGithubId(missingId))
			.isInstanceOf(UserNotFoundException.class)
			.hasMessage("사용자를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("withdraw: 사용자 삭제 시 Repository에서 제거됨")
	void withdraw_removesUser() {
		// Given
		User user = User.builder()
			.githubId("git4")
			.name("User4")
			.email("u4@mail.com")
			.studentNumber("SN4")
			.gamjaBatch(3)
			.nickname("nick4")
			.position(null)
			.build();
		repository.save(user);
		assertThat(repository.existsByGithubId("git4")).isTrue();

		// When
		userService.withdraw(user);

		// Then
		assertThat(repository.existsByGithubId("git4")).isFalse();
	}
}
