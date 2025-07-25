package org.gamja.gamzatechblog.domain.like.service;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.like.model.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Pageable;

public interface LikeService {
	PagedResponse<LikeResponse> getMyLikes(User user, Pageable pageable);

	LikeResponse likePost(User currentUser, Long postId);

	void unlikePost(User currentUser, Long postId);

	boolean isPostLiked(User currentUser, Long postId);
}
