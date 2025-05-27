package org.gamja.gamzatechblog.domain.commithistory.model.mapper;

import org.gamja.gamzatechblog.domain.commithistory.model.dto.response.CommitHistoryResponse;
import org.gamja.gamzatechblog.domain.commithistory.model.entity.CommitHistory;
import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommitHistoryMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "post", expression = "java(post)")
	@Mapping(target = "githubRepo", expression = "java(repo)")
	@Mapping(source = "request.commitMessage", target = "commitMessage")
	@Mapping(source = "sha", target = "commitSha")
	CommitHistory mapToCommitHistory(Post post, GitHubRepo repo, PostRequest request, String sha);

	@Mapping(source = "commitSha", target = "sha")
	@Mapping(source = "commitMessage", target = "message")
	CommitHistoryResponse mapToCommitHistoryResponse(CommitHistory entity);
}