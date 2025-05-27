package org.gamja.gamzatechblog.domain.commithistory.service;

import java.util.List;

import org.gamja.gamzatechblog.domain.commithistory.model.dto.CommitHistoryResponse;

public interface CommitHistoryService {
	List<CommitHistoryResponse> getHistoriesByPostId(Long postId);

	void saveHistory(Long postId, Long repoId, String sha, String message);
}
