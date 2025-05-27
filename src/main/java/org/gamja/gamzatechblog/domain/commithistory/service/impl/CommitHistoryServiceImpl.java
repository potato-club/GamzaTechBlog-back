package org.gamja.gamzatechblog.domain.commithistory.service.impl;

import java.util.List;

import org.gamja.gamzatechblog.domain.commithistory.model.dto.response.CommitHistoryResponse;
import org.gamja.gamzatechblog.domain.commithistory.model.entity.CommitHistory;
import org.gamja.gamzatechblog.domain.commithistory.model.mapper.CommitHistoryMapper;
import org.gamja.gamzatechblog.domain.commithistory.repository.CommitHistoryRepository;
import org.gamja.gamzatechblog.domain.commithistory.service.CommitHistoryService;
import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommitHistoryServiceImpl implements CommitHistoryService {
	private final CommitHistoryRepository commitHistoryRepository;
	private final CommitHistoryMapper commitHistoryMapper;

	@Override
	@Transactional(readOnly = true)
	public List<CommitHistoryResponse> getCommitHistoryListByPostId(Long postId) {
		List<CommitHistory> entities = commitHistoryRepository.findByPostId(postId);
		return entities.stream()
			.map(commitHistoryMapper::mapToCommitHistoryResponse)
			.toList();
	}

	@Override
	public void registerCommitHistory(Post post, GitHubRepo repo, PostRequest request, String sha) {
		CommitHistory entity = commitHistoryMapper.mapToCommitHistory(post, repo, request, sha);
		commitHistoryRepository.save(entity);
	}

}
