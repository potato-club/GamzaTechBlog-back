package org.gamja.gamzatechblog.domain.intro.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.intro.service.port.IntroductionFakeRepository;
import org.gamja.gamzatechblog.domain.introduction.model.dto.response.IntroResponse;
import org.gamja.gamzatechblog.domain.introduction.model.entity.Introduction;
import org.gamja.gamzatechblog.domain.introduction.model.mapper.IntroMapper;
import org.gamja.gamzatechblog.domain.introduction.service.IntroService;
import org.gamja.gamzatechblog.domain.introduction.service.impl.IntroServiceImpl;
import org.gamja.gamzatechblog.domain.introduction.service.port.IntroductionRepository;
import org.gamja.gamzatechblog.domain.introduction.validator.IntroValidator;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.support.intro.IntroFixtures;
import org.gamja.gamzatechblog.support.user.UserFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

@DisplayName("IntroService 단위 테스트")
class IntroServiceTest {

	private IntroService introService;
	private IntroductionRepository introductionRepository;
	private IntroMapper introMapper;
	private IntroValidator introValidator;

	@BeforeEach
	void setUp() {
		introductionRepository = new IntroductionFakeRepository();
		introMapper = mock(IntroMapper.class);
		introValidator = mock(IntroValidator.class);

		introService = new IntroServiceImpl(
			introductionRepository,
			introMapper,
			introValidator
		);
	}

	@Test
	@DisplayName("소개글 생성 성공")
	void create_success() {
		User user = UserFixtures.user("git-a");
		String content = IntroFixtures.content("첫번째");

		Introduction newIntro = mock(Introduction.class);
		IntroResponse expected = mock(IntroResponse.class);

		doNothing().when(introValidator).validateNotExists(user);
		when(introMapper.newIntroduction(user, content)).thenReturn(newIntro);
		when(introMapper.toResponse(newIntro)).thenReturn(expected);

		IntroResponse result = introService.create(user, content);

		assertThat(result).isSameAs(expected);
		verify(introValidator).validateNotExists(user);
		verify(introMapper).newIntroduction(user, content);
		verify(introMapper).toResponse(newIntro);
	}

	@Test
	@DisplayName("소개글 목록 페이징 조회")
	void list_success() {
		User user = UserFixtures.user("git-list");
		introductionRepository.save(IntroFixtures.introduction(user, "첫번째"));
		introductionRepository.save(IntroFixtures.introduction(user, "두번째"));

		PagedResponse<IntroResponse> page0 = introService.list(PageRequest.of(0, 10));

		assertThat(page0.totalElements()).isEqualTo(2);
	}

	@Test
	@DisplayName("소개글 삭제 성공")
	void delete_success() {
		User user = UserFixtures.user("git-z");
		Introduction intro = mock(Introduction.class);

		introductionRepository.save(intro);

		when(introValidator.validateExists(1L)).thenReturn(intro);
		doNothing().when(introValidator).validateOwner(intro, user);

		introService.delete(1L, user);

		assertThat(introductionRepository.findAll(PageRequest.of(0, 10)).getTotalElements()).isZero();
		verify(introValidator).validateExists(1L);
		verify(introValidator).validateOwner(intro, user);
	}
}
