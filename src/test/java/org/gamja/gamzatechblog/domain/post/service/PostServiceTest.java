package org.gamja.gamzatechblog.domain.post.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.core.auth.jwt.validator.GithubTokenValidator;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentResponse;
import org.gamja.gamzatechblog.domain.comment.service.CommentService;
import org.gamja.gamzatechblog.domain.commithistory.repository.CommitHistoryRepository;
import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostDetailResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostListResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostPopularResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostResponse;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.model.mapper.PostMapper;
import org.gamja.gamzatechblog.domain.post.model.mapper.impl.PostDetailMapper;
import org.gamja.gamzatechblog.domain.post.model.mapper.impl.PostPopularMapper;
import org.gamja.gamzatechblog.domain.post.service.impl.PostServiceImpl;
import org.gamja.gamzatechblog.domain.post.service.port.PostFakeRepository;
import org.gamja.gamzatechblog.domain.post.service.port.PostQueryPort;
import org.gamja.gamzatechblog.domain.post.service.port.PostRepository;
import org.gamja.gamzatechblog.domain.post.validator.PostValidator;
import org.gamja.gamzatechblog.domain.postimage.service.PostImageService;
import org.gamja.gamzatechblog.domain.posttag.util.PostTagUtil;
import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.gamja.gamzatechblog.domain.repository.port.GitHubRepoRepository;
import org.gamja.gamzatechblog.domain.tag.service.TagService;
import org.gamja.gamzatechblog.domain.tag.service.port.TagRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.support.post.PostFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class PostServiceTest {

	// Fake
	private PostRepository postRepository;

	// Mocks
	private PostMapper postMapper;
	private PostValidator postValidator;
	private GithubTokenValidator githubTokenValidator;
	private GitHubRepoRepository gitHubRepoRepository;
	private PostTagUtil postTagUtil;
	private TagRepository tagRepository;
	private CommentService commentService;
	private PostDetailMapper postDetailMapper;
	private PostQueryPort postQueryPort;
	private PostPopularMapper postPopularMapper;
	private PostImageService postImageService;
	private PostProcessingService postProcessingService;
	private CommitHistoryRepository commitHistoryRepository;

	private TagService tagService;

	private CacheManager cacheManager;

	private PostServiceImpl service;

	private User user;

	@BeforeEach
	void setUp() {
		postRepository = new PostFakeRepository();

		postMapper = mock(PostMapper.class);
		postValidator = mock(PostValidator.class);
		githubTokenValidator = mock(GithubTokenValidator.class);
		gitHubRepoRepository = mock(GitHubRepoRepository.class);
		postTagUtil = mock(PostTagUtil.class);
		tagRepository = mock(TagRepository.class);
		commentService = mock(CommentService.class);
		postDetailMapper = mock(PostDetailMapper.class);
		postQueryPort = mock(PostQueryPort.class);
		postPopularMapper = mock(PostPopularMapper.class);
		postImageService = mock(PostImageService.class);
		postProcessingService = mock(PostProcessingService.class);
		commitHistoryRepository = mock(CommitHistoryRepository.class);
		tagService = mock(TagService.class);

		cacheManager = new ConcurrentMapCacheManager(
			"hotPosts", "postDetail", "postsList", "myPosts", "searchPosts", "postsByTag", "allTags"
		);

		service = new PostServiceImpl(
			postRepository, postMapper, postValidator, githubTokenValidator,
			gitHubRepoRepository, postTagUtil, tagRepository, commentService,
			postDetailMapper, postQueryPort, postPopularMapper, postImageService,
			postProcessingService, commitHistoryRepository, cacheManager, tagService
		);

		user = PostFixtures.user(1L);
		when(githubTokenValidator.validateAndGetGitHubAccessToken(any())).thenReturn("token");
	}

	@Test
	@DisplayName("publishPost: 새 포스트 저장, 태그 동기화, 응답 매핑 수행")
	void publishPost_savesAndSyncsTags() {
		// given
		PostRequest req = mock(PostRequest.class);
		when(req.title()).thenReturn("T1");
		when(req.content()).thenReturn("C1");
		when(req.tags()).thenReturn(List.of("java", "spring"));
		when(req.commitMessage()).thenReturn("init");

		when(gitHubRepoRepository.findByUser(user)).thenReturn(java.util.Optional.empty());
		GitHubRepo repo = PostFixtures.repo(user, "GamzaTechBlog");
		when(gitHubRepoRepository.gitHubRepoSave(any(GitHubRepo.class))).thenReturn(repo);

		Post draft = PostFixtures.draft(user, repo, "T1", "C1");
		when(postMapper.buildPostEntityFromRequest(user, repo, req)).thenReturn(draft);

		PostResponse resp = mock(PostResponse.class);
		when(postMapper.buildPostResponseFromEntity(any(Post.class))).thenReturn(resp);

		// when
		PostResponse result = service.publishPost(user, req);

		// then
		assertThat(result).isSameAs(resp);
		// 저장 되었는지 (Fake 내부 저장소 개수 확인)
		assertThat(((PostFakeRepository)postRepository).internalStore()).hasSize(1);
		verify(postTagUtil).syncPostTags(any(Post.class), eq(List.of("java", "spring")));
		verify(githubTokenValidator).validateAndGetGitHubAccessToken(any());
		// afterCommit에 등록된 작업은 단위테스트에서 보장 못하므로 호출 검증 스킵
	}

	@Test
	@DisplayName("revisePost: 소유 검증 후 제목/내용 수정, 이미지/태그 동기화 및 응답 매핑")
	void revisePost_updatesAndSyncs() throws Exception {
		// given: 기존 포스트 저장
		Post existing = PostFixtures.post(null, user, "old", "oldC");
		((PostFakeRepository)postRepository).save(existing);
		setField(existing, "postTags", new ArrayList<>());

		when(postValidator.validatePostExists(existing.getId())).thenReturn(existing);
		doNothing().when(postValidator).validateOwnership(existing, user);

		PostRequest req = mock(PostRequest.class);
		when(req.title()).thenReturn("newT");
		when(req.content()).thenReturn("newC");
		when(req.tags()).thenReturn(List.of("tag1", "tag2"));
		when(req.commitMessage()).thenReturn("revise");

		PostResponse resp = mock(PostResponse.class);
		when(postMapper.buildPostResponseFromEntity(existing)).thenReturn(resp);

		// when
		PostResponse result = service.revisePost(user, existing.getId(), req);

		// then
		assertThat(result).isSameAs(resp);
		assertThat(existing.getTitle()).isEqualTo("newT");
		assertThat(existing.getContent()).isEqualTo("newC");
		verify(postImageService).syncImages(existing);
		verify(postTagUtil).syncPostTags(existing, List.of("tag1", "tag2"));
		verify(githubTokenValidator).validateAndGetGitHubAccessToken(any());
	}

	@Test
	@DisplayName("removePost: 소유 검증 후 커밋/이미지/태그 정리 및 삭제")
	void removePost_deletesAndCleans() {
		// given
		Post existing = PostFixtures.post(null, user, "toDel", "content");
		// postTags 빈 리스트 주입
		setField(existing, "postTags", new ArrayList<>());
		((PostFakeRepository)postRepository).save(existing);

		when(postValidator.validatePostExists(existing.getId())).thenReturn(existing);
		doNothing().when(postValidator).validateOwnership(existing, user);

		// when
		service.removePost(user, existing.getId());

		// then
		assertThat(((PostFakeRepository)postRepository).internalStore()).isEmpty();
		verify(commitHistoryRepository).deleteByPost(existing);
		verify(postImageService).deleteImagesForPost(existing);
		verify(tagRepository).deleteOrphanTags();
		verify(githubTokenValidator).validateAndGetGitHubAccessToken(any());
	}

	@Test
	@DisplayName("getPostDetail: 포스트와 댓글을 묶어 상세 응답으로 매핑")
	void getPostDetail_returnsDetail() {
		// given
		Post existing = PostFixtures.post(null, user, "t", "c");
		((PostFakeRepository)postRepository).save(existing);

		when(postValidator.validatePostExists(existing.getId())).thenReturn(existing);
		when(commentService.getCommentsByPostId(existing.getId()))
			.thenReturn(List.of(mock(CommentResponse.class)));

		PostDetailResponse detail = mock(PostDetailResponse.class);
		when(postDetailMapper.toDetailResponse(any(Post.class), anyList())).thenReturn(detail);

		// when
		PostDetailResponse result = service.getPostDetail(existing.getId());

		// then
		assertThat(result).isSameAs(detail);
		verify(commentService).getCommentsByPostId(existing.getId());
		verify(postDetailMapper).toDetailResponse(eq(existing), anyList());
	}

	@Test
	@DisplayName("getMyPosts/searchPosts/getPostsByTag: QueryPort 결과를 PagedResponse로 변환")
	void pagingEndpoints_useQueryPort() {
		// given
		var pageable = PageRequest.of(0, 2);
		var list = List.<PostListResponse>of(mock(PostListResponse.class), mock(PostListResponse.class));
		Page<PostListResponse> page = new PageImpl<>(list, pageable, 5);

		when(postQueryPort.findMyPosts(pageable, user)).thenReturn(page);
		when(postQueryPort.searchPostsByTitle(pageable, "spring")).thenReturn(page);
		when(postQueryPort.findAllPosts(pageable, List.of("java"))).thenReturn(page);

		// when
		PagedResponse<PostListResponse> my = service.getMyPosts(user, pageable);
		PagedResponse<PostListResponse> search = service.searchPostsByTitle(pageable, "spring");
		PagedResponse<PostListResponse> byTag = service.getPostsByTag("java", pageable);

		// then
		assertThat(my.content()).hasSize(2);
		assertThat(search.content()).hasSize(2);
		assertThat(byTag.content()).hasSize(2);
		verify(postQueryPort).findMyPosts(pageable, user);
		verify(postQueryPort).searchPostsByTitle(pageable, "spring");
		verify(postQueryPort).findAllPosts(pageable, List.of("java"));
	}

	@Test
	@DisplayName("getWeeklyPopularPosts: 인기 포스트를 PopularResponse로 매핑")
	void weeklyPopular_maps() {
		// given
		Post p1 = PostFixtures.post(null, user, "t1", "c1");
		Post p2 = PostFixtures.post(null, user, "t2", "c2");
		when(postQueryPort.findWeeklyPopularPosts(any(), eq(3))).thenReturn(List.of(p1, p2));

		PostPopularResponse r1 = mock(PostPopularResponse.class);
		PostPopularResponse r2 = mock(PostPopularResponse.class);
		when(postPopularMapper.toPopularResponse(p1)).thenReturn(r1);
		when(postPopularMapper.toPopularResponse(p2)).thenReturn(r2);

		// when
		List<PostPopularResponse> list = service.getWeeklyPopularPosts();

		// then
		assertThat(list).containsExactly(r1, r2);
		verify(postQueryPort).findWeeklyPopularPosts(any(), eq(3));
	}

	private static void setField(Object target, String name, Object value) {
		try {
			Field f = target.getClass().getDeclaredField(name);
			f.setAccessible(true);
			f.set(target, value);
		} catch (Exception ignore) {
		}
	}
}
