package org.gamja.gamzatechblog.domain.post.service;

import java.util.List;

import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.service.port.PostRepository;
import org.gamja.gamzatechblog.domain.post.util.PostUtil;
import org.gamja.gamzatechblog.domain.postimage.service.PostImageService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostProcessingService {

	private final PostRepository postRepository;
	private final PostImageService postImageService;
	private final PostUtil postUtil;

	@Async("postExecutor")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void processPostPublishing(Long postId, String githubToken, String commitMessage, List<String> tags) {
		try {
			log.info("게시물 백그라운드 처리 시작: postId={}", postId);
			Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalStateException("게시물을 찾을 수 없습니다 id:  " + postId)); // 예외 처리 추가예정 validator로
			postImageService.syncImages(post);
			String sha = postUtil.syncToGitHub(githubToken, null, null, post, tags, "Add", commitMessage);
			log.info("게시물 백그라운드 처리 완료: postId={}", postId);
		} catch (Exception e) {
			log.error("게시물 백그라운드 처리 중 오류 발생: postId={}", postId, e);
		}
	}
}