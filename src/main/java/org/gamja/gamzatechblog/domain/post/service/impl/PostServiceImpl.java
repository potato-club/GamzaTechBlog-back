package org.gamja.gamzatechblog.domain.post.service.impl;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.core.auth.jwt.validator.GithubTokenValidator;
import org.gamja.gamzatechblog.domain.comment.model.dto.response.CommentResponse;
import org.gamja.gamzatechblog.domain.comment.service.CommentService;
import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostDetailResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostListResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostResponse;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.model.mapper.PostMapper;
import org.gamja.gamzatechblog.domain.post.model.mapper.impl.PostDetailMapper;
import org.gamja.gamzatechblog.domain.post.model.mapper.impl.PostListMapper;
import org.gamja.gamzatechblog.domain.post.repository.PostRepository;
import org.gamja.gamzatechblog.domain.post.repository.PostRepositoryCustom;
import org.gamja.gamzatechblog.domain.post.service.PostService;
import org.gamja.gamzatechblog.domain.post.util.PostUtil;
import org.gamja.gamzatechblog.domain.post.validator.PostValidator;
import org.gamja.gamzatechblog.domain.posttag.util.PostTagUtil;
import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.gamja.gamzatechblog.domain.repository.repository.GitHubRepoRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {
	private final PostRepository postRepository;
	private final PostMapper postMapper;
	private final PostValidator postValidator;
	private final GithubTokenValidator githubTokenValidator;
	private final GitHubRepoRepository githubRepoRepository;
	private final PostUtil postUtil;
	private final PostTagUtil postTagUtil;
	private final PostListMapper postListMapper;
	private final CommentService commentService;
	private final PostDetailMapper postDetailMapper;
	private final PostRepositoryCustom postRepositoryCustom;

	@Override
	public PostResponse publishPost(User currentUser, PostRequest request) {
		String token = githubTokenValidator.validateAndGetGitHubAccessToken(currentUser.getGithubId());
		//개인 레포지토리 없으면 생성 GamzaTechBlog
		String repoName = "GamzaTechBlog";
		GitHubRepo repo = githubRepoRepository.findByUser(currentUser)
			.orElseGet(() ->
				githubRepoRepository.save(GitHubRepo.builder()
					.user(currentUser)
					.name(repoName)
					.githubUrl("https://github.com/" + currentUser.getNickname() + "/" + repoName)
					.build()
				)
			);
		Post post = postMapper.buildPostEntityFromRequest(currentUser, repo, request);
		post = postRepository.save(post);
		postTagUtil.syncPostTags(post, request.getTags());
		postUtil.syncToGitHub(token, null, null, post, request.getTags(), "Add", request.getCommitMessage());
		return postMapper.buildPostResponseFromEntity(post);
	}

	@Override
	public PostResponse revisePost(User currentUser, Long postId, PostRequest request) {
		Post post = postValidator.validatePostExists(postId);
		postValidator.validateOwnership(post, currentUser);
		List<String> prevTags = post.getPostTags().stream()
			.map(pt -> pt.getTag().getTagName())
			.toList();
		String prevTitle = post.getTitle();
		post.update(request.getTitle(), request.getContent());
		postRepository.save(post);
		postTagUtil.syncPostTags(post, request.getTags());
		String token = githubTokenValidator.validateAndGetGitHubAccessToken(currentUser.getGithubId());
		postUtil.syncToGitHub(token, prevTitle, prevTags, post, request.getTags(), "Update",
			request.getCommitMessage());
		return postMapper.buildPostResponseFromEntity(post);
	}

	@Override
	public void removePost(User currentUser, Long postId) {
		Post post = postValidator.validatePostExists(postId);
		postValidator.validateOwnership(post, currentUser);
		String token = githubTokenValidator.validateAndGetGitHubAccessToken(currentUser.getGithubId());
		String prevTitle = post.getTitle();
		List<String> prevTags = post.getPostTags().stream()
			.map(pt -> pt.getTag().getTagName())
			.toList();
		postUtil.syncToGitHub(token, prevTitle, prevTags, post, null, "Delete", post.getCommitMessage());
		postRepository.delete(post);
	}

	@Override
	public PagedResponse<PostListResponse> getPosts(Pageable pageable, List<String> filterTags) {
		Page<PostListResponse> page = postRepositoryCustom
			.findAllPosts(pageable, filterTags)
			.map(postListMapper::toListResponse);

		return PagedResponse.pagedFrom(page);
	}

	@Override
	public PostDetailResponse getPostDetail(Long postId) {
		Post post = postValidator.validatePostExists(postId);
		List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
		return postDetailMapper.toDetailResponse(post, comments);
	}
}
