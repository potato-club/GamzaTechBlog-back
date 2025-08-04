package org.gamja.gamzatechblog.domain.user.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserActivityResponse(

	@Schema(description = "좋아요 한 게시물 수", example = "5")
	int likedPostCount,

	@Schema(description = "작성한 게시물 수", example = "10")
	int writtenPostCount,

	@Schema(description = "작성한 댓글 수", example = "20")
	int writtenCommentCount
) {
}
