package org.gamja.gamzatechblog.domain.commithistory.service;

import java.util.List;

import org.gamja.gamzatechblog.domain.commithistory.model.dto.response.CommitHistoryResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;

public interface CommitHistoryService {
	List<CommitHistoryResponse> getCommitHistoryListByPostId(Long postId);

	void registerCommitHistory(Post post, GitHubRepo repo, PostRequest request, String sha);
}
