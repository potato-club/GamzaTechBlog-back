package org.gamja.gamzatechblog.domain.like.service.port;

import static org.assertj.core.api.Assertions.*;
import static org.gamja.gamzatechblog.support.like.LikeFixtures.*;

import java.util.Optional;

import org.gamja.gamzatechblog.domain.like.model.entity.Like;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeFakeRepository 메서드 단위 테스트")
class LikeFakeRepositoryTest {

	private LikeFakeRepository likeFakeRepository;

	@BeforeEach
	void setUp() {
		likeFakeRepository = new LikeFakeRepository();
	}

	@Test
	@DisplayName("좋아요가 존재하지 않을 때, 새로운 좋아요를 저장")
	void save_whenLikeDoesNotExist_thenSavesNewLike() {
		Like newLike = createLike(USER, POST_1);

		Like savedLike = likeFakeRepository.saveLike(newLike);

		assertThat(savedLike.getId()).isNotNull();
		assertThat(savedLike.getUser()).isEqualTo(USER);
		assertThat(savedLike.getPost()).isEqualTo(POST_1);
	}

	@Test
	@DisplayName("사용자와 게시물로 좋아요를 조회할 때, 좋아요가 존재하면 해당 좋아요를 반환")
	void findByUserAndPost_whenLikeExists_thenReturnsExistingLike() {
		likeFakeRepository.saveLike(LIKE_POST_1);

		Optional<Like> foundLike = likeFakeRepository.findByUserAndPost(USER, POST_1);

		assertThat(foundLike).isPresent();
		assertThat(foundLike.get().getPost()).isEqualTo(POST_1);
		assertThat(foundLike.get().getUser()).isEqualTo(USER);
	}

	@Test
	@DisplayName("사용자와 게시물로 좋아요를 조회할 때, 좋아요 없으면 빈 반환")
	void findByUserAndPost_whenLikeDoesNotExist_thenReturnsEmptyOptional() {
		// 좋아요가 없는 상황

		Optional<Like> foundLike = likeFakeRepository.findByUserAndPost(USER, POST_1);

		assertThat(foundLike).isEmpty();
	}

	@Test
	@DisplayName("사용자 게시물에 좋아요 삭제시 완전 삭제된다.")
	void deleteByUserAndPost_whenLikeExists_thenCompletelyDeletesLike() {
		likeFakeRepository.saveLike(LIKE_POST_1);

		likeFakeRepository.deleteByUserAndPost(USER, POST_1);

		Optional<Like> foundLike = likeFakeRepository.findByUserAndPost(USER, POST_1);
		assertThat(foundLike).isEmpty();
	}

	@Test
	@DisplayName("특정 사용자가 좋아요 갯수 조회시 정확하게 반환한다.")
	void countByUser_whenMultipleLikesExist_thenReturnsCorrectCount() {
		likeFakeRepository.saveLike(createLike(USER, POST_1));
		likeFakeRepository.saveLike(createLike(USER, POST_2));

		int actualCount = likeFakeRepository.countByUser(USER);

		assertThat(actualCount).isEqualTo(2);
	}

	@Test
	@DisplayName("좋아요가 없는 사용자의 좋아요 개수를 조회하면, 0을 반환")
	void countByUser_whenNoLikesExist_thenReturnsZero() {
		// 좋아요가 없는 상황

		int actualCount = likeFakeRepository.countByUser(USER);

		assertThat(actualCount).isEqualTo(0);
	}
}