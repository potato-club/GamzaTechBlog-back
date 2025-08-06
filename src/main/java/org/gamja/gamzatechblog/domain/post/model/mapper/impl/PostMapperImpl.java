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
		return request.toPostEntity(currentUser, repo);
	}

	@Override
	public PostResponse buildPostResponseFromEntity(Post post) {
		return PostResponse.fromPostEntity(post);
	}
}