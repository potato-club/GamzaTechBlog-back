package org.gamja.gamzatechblog.domain.post.service;

import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public interface PostService {
	PostResponse publishPost(User currentUser, PostRequest request);

	PostResponse revisePost(User currentUser, Long postId, PostRequest request);

	void removePost(User currentUser, Long postId);
}
