package org.gamja.gamzatechblog.domain.post.model.dto.response;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.PagedResponse;

public record HomeFeedResponse(
	List<PostPopularResponse> weeklyPopular,
	PagedResponse<PostListResponse> latest,
	List<String> allTags
) {
}
