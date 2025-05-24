package org.gamja.gamzatechblog.domain.post.service.impl;

import java.util.List;

import org.gamja.gamzatechblog.core.auth.jwt.validator.GithubTokenValidator;
import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostResponse;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.model.mapper.PostMapper;
import org.gamja.gamzatechblog.domain.post.repository.PostRepository;
import org.gamja.gamzatechblog.domain.post.service.PostService;
import org.gamja.gamzatechblog.domain.post.util.PostUtil;
import org.gamja.gamzatechblog.domain.post.validator.PostValidator;
import org.gamja.gamzatechblog.domain.posttag.util.PostTagUtil;
import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.gamja.gamzatechblog.domain.repository.repository.GitHubRepoRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
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

	@Override
	public PostResponse publishPost(User currentUser, PostRequest request) {
		String token = githubTokenValidator.validateAndGetGitHubAccessToken(currentUser.getGithubId());
		// 1. 개인 레포지토리 없으면 생성 (이름: GamjaTechBlog)
		String repoName = "GamjaTechBlog";
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
		// 3. GitHub에 Add
		postUtil.syncToGitHub(token, currentUser.getNickname(), post, request.getTags(), "Add");
		return postMapper.buildPostResponseFromEntity(post);
	}

	@Override
	public PostResponse revisePost(User currentUser, Long postId, PostRequest request) {
		Post post = postValidator.validatePostExists(postId);
		postValidator.validateOwnership(post, currentUser);
		post.update(request.getTitle(), request.getContent(), request.getTags());
		postRepository.save(post);
		postTagUtil.syncPostTags(post, request.getTags());
		// GitHub 동기화 (Update)
		String token = githubTokenValidator.validateAndGetGitHubAccessToken(currentUser.getGithubId());
		postUtil.syncToGitHub(token, currentUser.getNickname(), post, request.getTags(), "Update");
		return postMapper.buildPostResponseFromEntity(post);
	}

	@Override
	public void removePost(User currentUser, Long postId) {
		Post post = postValidator.validatePostExists(postId);
		postValidator.validateOwnership(post, currentUser);
		// GitHub 동기화 (Delete)
		String token = githubTokenValidator.validateAndGetGitHubAccessToken(currentUser.getGithubId());
		List<String> tags = post.getPostTags().stream()
			.map(pt -> pt.getTag().getTagName())
			.toList();
		postUtil.syncToGitHub(token, currentUser.getNickname(), post, tags, "Delete");
		postRepository.delete(post);
	}
}
