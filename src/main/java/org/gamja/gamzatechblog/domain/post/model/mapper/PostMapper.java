package org.gamja.gamzatechblog.domain.post.model.mapper;

import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostResponse;
import org.gamja.gamzatechblog.domain.post.model.entity.Post;
import org.gamja.gamzatechblog.domain.repository.model.entity.GitHubRepo;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public interface PostMapper {
	Post buildPostEntityFromRequest(User currentUser, GitHubRepo repo, PostRequest request);

	PostResponse buildPostResponseFromEntity(Post post);

}
