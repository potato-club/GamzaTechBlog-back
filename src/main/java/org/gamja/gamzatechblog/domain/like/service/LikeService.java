package org.gamja.gamzatechblog.domain.like.service;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.like.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Pageable;

public interface LikeService {
	// List<LikeResponse> getMyLikes(User currentUser);

	PagedResponse<LikeResponse> getMyLikes(User currentUser, Pageable pageable);

	LikeResponse likePost(User currentUser, Long postId);

	void unlikePost(User currentUser, Long postId);
}
