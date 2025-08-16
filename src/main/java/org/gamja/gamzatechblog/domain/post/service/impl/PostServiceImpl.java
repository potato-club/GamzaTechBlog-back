package org.gamja.gamzatechblog.domain.post.service.impl;

import static org.gamja.gamzatechblog.domain.post.util.TxSync.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
import org.gamja.gamzatechblog.domain.post.service.PostProcessingService;
import org.gamja.gamzatechblog.domain.post.service.PostService;
import org.gamja.gamzatechblog.domain.post.service.port.PostQueryPort;
import org.gamja.gamzatechblog.domain.post.service.port.PostRepository;
import org.gamja.gamzatechblog.domain.post.validator.PostValidator;
import org.gamja.gamzatechblog.domain.postimage.service.PostImageService;
import org.gamja.gamzatechblog.domain.posttag.util.PostTagUtil;
import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.gamja.gamzatechblog.domain.repository.port.GitHubRepoRepository;
import org.gamja.gamzatechblog.domain.tag.service.port.TagRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostServiceImpl implements PostService {

	private static final String REPO_NAME = "GamzaTechBlog";

	private final PostRepository postRepository;
	private final PostMapper postMapper;
	private final PostValidator postValidator;
	private final GithubTokenValidator githubTokenValidator;
	private final GitHubRepoRepository githubRepoRepository;
	private final PostTagUtil postTagUtil;
	private final TagRepository tagRepository;
	private final CommentService commentService;
	private final PostDetailMapper postDetailMapper;
	private final PostQueryPort postQueryPort;
	private final PostPopularMapper postPopularMapper;
	private final PostImageService postImageService;
	private final PostProcessingService postProcessingService;
	private final CommitHistoryRepository commitHistoryRepository;
	private final CacheManager cacheManager;

	@Override
	@CacheEvict(value = {"hotPosts", "postDetail", "postsList", "myPosts", "searchPosts",
		"postsByTag"}, allEntries = true)
	public PostResponse publishPost(User currentUser, PostRequest request) {
		String token = githubTokenValidator.validateAndGetGitHubAccessToken(currentUser.getGithubId());
		GitHubRepo repo = ensureRepo(currentUser);
		Post draft = postMapper.buildPostEntityFromRequest(currentUser, repo, request);
		Post savedPost = postRepository.save(draft);
		postRepository.flush();
		postTagUtil.syncPostTags(savedPost, request.tags());
		afterCommit(() -> postProcessingService.processPostPublishing(
			savedPost.getId(), token, request.commitMessage(), request.tags()
		));

		evictAllTagsAfterCommit();

		return postMapper.buildPostResponseFromEntity(savedPost);
	}

	@Override
	@CacheEvict(value = {"hotPosts", "postDetail", "postsList", "myPosts", "searchPosts",
		"postsByTag"}, allEntries = true)
	public PostResponse revisePost(User currentUser, Long postId, PostRequest request) {
		Post post = postValidator.validatePostExists(postId);
		postValidator.validateOwnership(post, currentUser);

		List<String> prevTags = tagNamesOf(post);
		String prevTitle = post.getTitle();
		post.update(request.title(), request.content());
		postRepository.save(post);
		postTagUtil.syncPostTags(post, request.tags());
		postImageService.syncImages(post);
		String token = githubTokenValidator.validateAndGetGitHubAccessToken(currentUser.getGithubId());
		afterCommit(() -> postProcessingService.processPostRevision(
			post.getId(), token, prevTitle, prevTags, request
		));

		evictAllTagsAfterCommit();
		return postMapper.buildPostResponseFromEntity(post);
	}

	@Override
	@CacheEvict(value = {"hotPosts", "postDetail", "postsList", "myPosts", "searchPosts",
		"postsByTag"}, allEntries = true)
	@Transactional(noRollbackFor = HttpClientErrorException.NotFound.class)
	public void removePost(User currentUser, Long postId) {
		Post post = postValidator.validatePostExists(postId);
		postValidator.validateOwnership(post, currentUser);

		String prevTitle = post.getTitle();
		List<String> prevTags = tagNamesOf(post);
		String token = githubTokenValidator.validateAndGetGitHubAccessToken(currentUser.getGithubId());

		commitHistoryRepository.deleteByPost(post);
		postImageService.deleteImagesForPost(post);

		postRepository.delete(post);
		tagRepository.deleteOrphanTags();

		afterCommit(() -> postProcessingService.processPostDeletion(
			postId, token, currentUser.getNickname(), prevTitle, prevTags, null
		));

		evictAllTagsAfterCommit();
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(
		value = "postsList",
		key = "#pageable.pageNumber + '-' + #pageable.pageSize +"
			+ " '-' +(#filterTags != null ? #filterTags.toString() : 'all')"
		, unless = "#result.content.isEmpty()")
	public PagedResponse<PostListResponse> getPosts(Pageable pageable, List<String> filterTags) {
		Page<PostListResponse> dtoPage = postQueryPort.findAllPosts(pageable, filterTags);
		return PagedResponse.pagedFrom(dtoPage);
	}

	@Override
	@Cacheable(
		value = "postDetail",
		key = "#postId",
		unless = "#result == null"
	)
	public PostDetailResponse getPostDetail(Long postId) {
		Post post = postValidator.validatePostExists(postId);
		List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
		return postDetailMapper.toDetailResponse(post, comments);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(
		value = "myPosts",
		key = "#currentUser.id + '-' + #pageable.pageNumber + '-' + #pageable.pageSize",
		unless = "#result.content.isEmpty()"
	)
	public PagedResponse<PostListResponse> getMyPosts(User currentUser, Pageable pageable) {
		Page<PostListResponse> dtoPage = postQueryPort.findMyPosts(pageable, currentUser);
		return PagedResponse.pagedFrom(dtoPage);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "hotPosts", key = "'weekly'", unless = "#result.isEmpty()")
	public List<PostPopularResponse> getWeeklyPopularPosts() {
		LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
		List<Post> posts = postQueryPort.findWeeklyPopularPosts(oneWeekAgo, 3);
		return posts.stream()
			.map(postPopularMapper::toPopularResponse)
			.toList();
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(
		value = "postsByTag",
		key = "#p0 + '-' + #p1.pageNumber + '-' + #p1.pageSize",
		unless = "#result.content.isEmpty()"
	)
	public PagedResponse<PostListResponse> getPostsByTag(String tagName, Pageable pageable) {
		List<String> filterTags = List.of(tagName);
		Page<PostListResponse> page = postQueryPort.findAllPosts(pageable, filterTags);
		return PagedResponse.pagedFrom(page);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(
		value = "searchPosts",
		key = "#keyword + '-' + #pageable.pageNumber + '-' + #pageable.pageSize",
		unless = "#result.content.isEmpty()"
	)
	public PagedResponse<PostListResponse> searchPostsByTitle(Pageable pageable, String keyword) {
		Page<PostListResponse> pageData = postQueryPort.searchPostsByTitle(pageable, keyword);
		return PagedResponse.pagedFrom(pageData);
	}

	private GitHubRepo ensureRepo(User user) {
		return githubRepoRepository.findByUser(user)
			.orElseGet(() ->
				githubRepoRepository.gitHubRepoSave(GitHubRepo.builder()
					.user(user)
					.name(REPO_NAME)
					.githubUrl("https://github.com/" + user.getNickname() + "/" + REPO_NAME)
					.build()
				)
			);
	}

	private List<String> tagNamesOf(Post post) {
		return post.getPostTags().stream()
			.map(pt -> pt.getTag())
			.filter(Objects::nonNull)
			.map(tag -> tag.getTagName())
			.filter(name -> name != null && !name.isBlank())
			.toList();
	}

	private void evictAllTagsAfterCommit() {
		afterCommit(() -> {
			Cache cache = cacheManager.getCache("allTags");
			if (cache != null) {
				cache.clear();
				log.debug("allTags 캐시를 커밋 후 무효화했습니다.");
			}
		});
	}
}

