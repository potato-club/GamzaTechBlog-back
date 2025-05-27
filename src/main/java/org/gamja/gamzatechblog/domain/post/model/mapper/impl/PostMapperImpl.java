package org.gamja.gamzatechblog.domain.post.model.mapper.impl;

import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostResponse;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.post.model.mapper.PostMapper;
import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PostMapperImpl implements PostMapper {
	@Override
	public Post buildPostEntityFromRequest(User currentUser, GitHubRepo repo, PostRequest request) {
		return Post.builder()
			.user(currentUser)
			.githubRepo(repo)
			.title(request.getTitle())
			.content(request.getContent())
			.commitMessage(request.getCommitMessage())
			.build();
	}

	@Override
	public PostResponse buildPostResponseFromEntity(Post post) {
		return PostResponse.builder()
			.postId(post.getId())
			.authorGithubId(post.getUser().getGithubId())
			.repositoryName(post.getGithubRepo().getName())
			.title(post.getTitle())
			.content(post.getContent())
			.tags(post.getPostTags().stream()
				.map(pt -> pt.getTag().getTagName())
				.toList())
			.commitMessage(post.getCommitMessage())
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.build();
	}
}