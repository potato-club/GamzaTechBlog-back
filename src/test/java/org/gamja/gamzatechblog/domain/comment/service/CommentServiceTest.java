package org.gamja.gamzatechblog.domain.comment.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentListResponse;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentResponse;
import org.gamja.gamzatechblog.domain.comment.model.entity.Comment;
import org.gamja.gamzatechblog.domain.comment.model.mapper.CommentMapper;
import org.gamja.gamzatechblog.domain.comment.service.impl.CommentServiceImpl;
import org.gamja.gamzatechblog.domain.comment.service.port.CommentFakeRepository;
import org.gamja.gamzatechblog.domain.comment.service.port.CommentRepository;
import org.gamja.gamzatechblog.domain.comment.validator.CommentValidator;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.validator.PostValidator;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.support.comment.CommentFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.data.domain.PageRequest;

class CommentServiceTest {

	private CommentRepository repo;
	private CommentMapper mapper;
	private CommentValidator commentValidator;
	private PostValidator postValidator;
	private CacheManager cacheManager;
	private CommentServiceImpl service;

	private User user1;
	private User user2;
	private Post post1;

	@BeforeEach
	void setUp() {
		repo = new CommentFakeRepository();
		mapper = Mockito.mock(CommentMapper.class);
		commentValidator = Mockito.mock(CommentValidator.class);
		postValidator = Mockito.mock(PostValidator.class);
		cacheManager = new ConcurrentMapCacheManager("postDetail");

		service = new CommentServiceImpl(repo, mapper, commentValidator, postValidator, cacheManager);

		user1 = CommentFixtures.user(1L);
		user2 = CommentFixtures.user(2L);
		post1 = CommentFixtures.post(10L, user1);
	}

	@Test
	@DisplayName("getCommentsByPostId: 루트 댓글들만 매퍼에 전달되어 트리 응답으로 반환된다")
	void getCommentsByPostId_returnsMappedRoots() {
		Comment rootA = ((CommentFakeRepository)repo).saveComment(
			CommentFixtures.comment(post1, user1, null, "A")
		);
		Comment rootB = ((CommentFakeRepository)repo).saveComment(
			CommentFixtures.comment(post1, user2, null, "B")
		);
		((CommentFakeRepository)repo).saveComment(
			CommentFixtures.comment(post1, user1, rootA, "A-1")
		);

		when(postValidator.validatePostExists(post1.getId())).thenReturn(post1);
		when(mapper.mapToCommentTree(any(Comment.class)))
			.thenReturn(mock(CommentResponse.class));

		List<CommentResponse> result = service.getCommentsByPostId(post1.getId());

		verify(mapper, times(2)).mapToCommentTree(any(Comment.class));
		assertThat(result).hasSize(2);
	}

	@Test
	@DisplayName("deleteComment: 소유 검증 후 삭제되고 캐시(postDetail)가 evict 된다")
	void deleteComment_deletesAndEvictsCache() {
		Comment existing = ((CommentFakeRepository)repo).saveComment(
			CommentFixtures.comment(post1, user1, null, "toDel")
		);

		when(commentValidator.validateCommentExists(existing.getId())).thenReturn(existing);
		doNothing().when(commentValidator).validateCommentOwnership(existing, user1);

		Cache cache = cacheManager.getCache("postDetail");
		cache.put(post1.getId(), "dummy");

		service.deleteComment(user1, existing.getId());

		assertThat(((CommentFakeRepository)repo).internalStore()).isEmpty();
		assertThat(cache.get(post1.getId())).isNull();
	}

	@Test
	@DisplayName("getMyComments: 페이지 요청에 맞춰 페이징되고 매퍼 변환을 거친다")
	void getMyComments_paging() {
		((CommentFakeRepository)repo).saveComment(CommentFixtures.comment(post1, user1, null, "c1"));
		((CommentFakeRepository)repo).saveComment(CommentFixtures.comment(post1, user1, null, "c2"));
		((CommentFakeRepository)repo).saveComment(CommentFixtures.comment(post1, user1, null, "c3"));
		((CommentFakeRepository)repo).saveComment(CommentFixtures.comment(post1, user2, null, "d1"));

		when(mapper.toCommentListResponse(any(Comment.class)))
			.thenReturn(mock(CommentListResponse.class));

		PagedResponse<CommentListResponse> page =
			service.getMyComments(user1, PageRequest.of(0, 2));

		assertThat(page.content()).hasSize(2);
		assertThat(page.totalElements()).isEqualTo(3);
		verify(mapper, times(2)).toCommentListResponse(any(Comment.class));
	}
}
