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

@DisplayName("UserServiceImpl 메서드 단위 테스트")
public class UserServiceImplTest {

	private static final String EXISTING_ID = "git2";
	private UserService userService;
	private UserFakeUserRepository repository;

	@BeforeEach
	void setUp() {
		repository = new UserFakeUserRepository();
		repository.save(createUser(EXISTING_ID));

		userService = new UserServiceImpl(
			repository,
			new UserMapper(),
			new UserValidator(repository),
			new UserProfileMapperImpl(),
			null, null, null
		);
	}

	private User createUser(String githubId) {
		return User.builder()
			.githubId(githubId)
			.name("User-" + githubId)
			.email(githubId + "@mail.com")
			.studentNumber("SN-" + githubId)
			.gamjaBatch(1)
			.nickname("nick-" + githubId)
			.position(null)
			.build();
	}

	@Test
	@DisplayName("깃허브 아이디가 저장된 ID일 때 true, 미저장 ID일 때 false 반환")
	void existsByGithubId_behavior() {
		assertThat(userService.existsByGithubId(EXISTING_ID)).isTrue();
		assertThat(userService.existsByGithubId("unknown")).isFalse();
	}

	@Test
	@DisplayName("유저의 아이디를 성공적으로 찾았을 경우")
	void findByGithubId_success() {
		User saved = repository.save(createUser("git123"));

		User result = userService.findByGithubId("git123");

		assertThat(result.getName()).isEqualTo(saved.getName());
	}

	@Test
	@DisplayName("존재하지 않는 아이디 검색시 404발생")
	void findByGithubId_notFound() {
		assertThatThrownBy(() -> userService.findByGithubId("none"))
			.isInstanceOf(UserNotFoundException.class)
			.hasMessage("사용자를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("사용자 삭제시 제거가 되는지")
	void withdraw_removesUser() {
		User existingUser = repository.findByGithubId(EXISTING_ID).orElse(null);
		assertThat(existingUser).isNotNull();

		userService.withdraw(existingUser);
		assertThat(repository.existsByGithubId(EXISTING_ID)).isFalse();
	}
}
