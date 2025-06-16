package org.gamja.gamzatechblog.domain.like.service;

import java.util.List;

import org.gamja.gamzatechblog.domain.like.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public interface LikeService {
	List<LikeResponse> getMyLikes(User currentUser);

	LikeResponse likePost(User currentUser, Long postId);

	void unlikePost(User currentUser, Long postId);
}
