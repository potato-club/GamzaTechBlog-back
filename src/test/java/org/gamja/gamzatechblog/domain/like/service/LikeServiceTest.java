package org.gamja.gamzatechblog.domain.like.service;

import static org.assertj.core.api.Assertions.*;
import static org.gamja.gamzatechblog.support.like.LikeFixtures.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.like.model.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.like.model.mapper.LikeMapper;
import org.gamja.gamzatechblog.domain.like.service.impl.LikeServiceImpl;
import org.gamja.gamzatechblog.domain.like.service.port.LikeFakeRepository;
import org.gamja.gamzatechblog.domain.like.service.port.LikeQueryPort;
import org.gamja.gamzatechblog.domain.like.validator.LikeValidator;
import org.gamja.gamzatechblog.domain.post.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeService 단위 테스트")
class LikeServiceTest {

	private LikeService likeService;
	private LikeFakeRepository likeRepository;

	@Mock
	private LikeMapper likeMapper;
	@Mock
	private PostValidator postValidator;
	@Mock
	private LikeValidator likeValidator;
	@Mock
	private LikeQueryPort likeQueryPort;

	@BeforeEach
	void setUp() {
		likeRepository = new LikeFakeRepository();
		likeService = new LikeServiceImpl(
			likeRepository,
			likeMapper,
			postValidator,
			likeValidator,
			likeQueryPort
		);
	}

	@Test
	@DisplayName("좋아요를 누르면, 데이터 저장")
	void whenValidRequest_thenSavesLike() {
		Long postId = 1L;
		when(postValidator.validatePostExists(postId)).thenReturn(POST_1);

		when(likeMapper.toLikeResponse(any())).thenReturn(
			new LikeResponse(
				1L,                      // likeId
				postId,                  // postId
				USER.getGithubId(),      // userId
				USER.getNickname(),      // nickname
				null,                    // profileImageUrl (없으면 null/"" 가능)
				POST_1.getTitle(),       // title
				POST_1.getContent(),     // content (필드 명이 다르면 아무 String)
				LocalDateTime.now(),     // createdAt
				List.of("tag1", "tag2")   // tags
			)
		);

		likeService.likePost(USER, postId);

		assertThat(likeRepository.findByUserAndPost(USER, POST_1)).isPresent();
	}

	@Test
	@DisplayName("좋아요를 취소하면, 데이터가 삭제된다")
	void whenLikeExists_thenDeletesLike() {
		Long postId = 1L;
		likeRepository.saveLike(LIKE_POST_1); // Fixture로 데이터 미리 저장
		when(postValidator.validatePostExists(postId)).thenReturn(POST_1);

		likeService.unlikePost(USER, postId);

		assertThat(likeRepository.findByUserAndPost(USER, POST_1)).isEmpty();
	}

	@Test
	@DisplayName("목록 조회를 요청하면, 조회된 결과를 반환")
	void whenCalled_thenReturnsPagedResponse() {
		Pageable pageable = PageRequest.of(0, 10);
		PagedResponse<LikeResponse> expectedResponse =
			new PagedResponse<>(Collections.emptyList(), 0, 10, 0L, 0);

		when(likeQueryPort.findMyLikesByUser(USER, pageable)).thenReturn(expectedResponse);

		PagedResponse<LikeResponse> actualResponse = likeService.getMyLikes(USER, pageable);

		assertThat(actualResponse).isEqualTo(expectedResponse);
	}
}
