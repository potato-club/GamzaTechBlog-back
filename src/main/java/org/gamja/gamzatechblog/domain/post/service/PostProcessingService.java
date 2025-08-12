package org.gamja.gamzatechblog.domain.post.service;

import java.util.List;

import org.gamja.gamzatechblog.domain.commithistory.service.CommitHistoryService;
import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.service.port.PostRepository;
import org.gamja.gamzatechblog.domain.post.util.PostUtil;
import org.gamja.gamzatechblog.domain.post.util.github.GitDeleteCmd;
import org.gamja.gamzatechblog.domain.post.util.github.GitSyncCmd;
import org.gamja.gamzatechblog.domain.postimage.service.PostImageService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException.NotFound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostProcessingService {

	private final PostRepository postRepository;
	private final PostUtil postUtil;
	private final PostImageService postImageService;
	private final CommitHistoryService commitHistoryService;

	@Async("postExecutor")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@CacheEvict(value = "postDetail", key = "#p0")
	public void processPostPublishing(Long postId, String githubToken, String commitMessage, List<String> tags) {
		try {
			log.info("게시물 백그라운드 처리 시작: postId={}", postId);

			Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalStateException("게시물을 찾을 수 없습니다 id: " + postId));

			postImageService.syncImages(post);

			String sha = postUtil.syncToGitHub(GitSyncCmd.add(githubToken, post, tags, commitMessage));

			List<String> safeTags = (tags == null) ? List.of() : List.copyOf(tags);

			PostRequest historyReq = new PostRequest(
				post.getTitle(),
				post.getContent(),
				safeTags,
				commitMessage
			);
			commitHistoryService.registerCommitHistory(post, post.getGithubRepo(), historyReq, sha);

			log.info("게시물 백그라운드 처리 완료: postId={}, sha={}", postId, sha);
		} catch (Exception e) {
			log.error("게시물 백그라운드 처리 중 오류 발생: postId={}", postId, e);
		}
	}

	@Async("postExecutor")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@CacheEvict(value = "postDetail", key = "#postId")
	public void processPostRevision(Long postId, String githubToken, String prevTitle, List<String> prevTags,
		PostRequest req) {
		try {
			log.info("게시물 수정 백그라운드 처리 시작: postId={}", postId);
			Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalStateException("게시물을 찾을 수 없습니다 id: " + postId));

			postImageService.syncImages(post);

			String sha = postUtil.syncToGitHub(
				GitSyncCmd.update(githubToken, prevTitle, prevTags, post, req.tags(), req.commitMessage()));
			commitHistoryService.registerCommitHistory(post, post.getGithubRepo(), req, sha);
			log.info("게시물 수정 백그라운드 처리 완료: postId={}, sha={}", postId, sha);
		} catch (Exception e) {
			log.error("게시물 수정 백그라운드 처리 중 오류: postId={}", postId, e);
		}
	}

	@Async("postExecutor")
	@Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = NotFound.class)
	@CacheEvict(value = "postDetail", key = "#postId", condition = "#postId != null")
	public void processPostDeletion(Long postId, String githubToken, String owner, String prevTitle,
		List<String> prevTags, String commitMessage) {
		try {
			log.info("게시물 삭제 백그라운드 처리 시작: postId={}", postId);
			postUtil.deleteFromGitHub(GitDeleteCmd.of(githubToken, owner, postId, prevTitle, prevTags, commitMessage));
			log.info("게시물 삭제 백그라운드 처리 완료: postId={}", postId);
		} catch (NotFound e) {
			log.warn("GitHub 404 -> 무시 (path={}, postId={})", prevTitle, postId);
		} catch (Exception e) {
			log.error("게시물 삭제 백그라운드 처리 중 오류: postId={}", postId, e);
		}
	}
}
