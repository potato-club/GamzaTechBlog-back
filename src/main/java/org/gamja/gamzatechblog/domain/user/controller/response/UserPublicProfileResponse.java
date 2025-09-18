package org.gamja.gamzatechblog.domain.user.controller.response;

import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostListResponse;

public record UserPublicProfileResponse(
	UserMiniProfileResponse profile,
	UserActivityResponse activity,
	PagedResponse<PostListResponse> posts
) {
}