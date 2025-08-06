package org.gamja.gamzatechblog.domain.like.controller.docs;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.domain.like.model.dto.response.LikeResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
public interface LikeControllerSwagger {

	@Operation(
		summary = "내가 누른 좋아요 목록 조회",
		tags = "좋아요 기능",
		responses = @ApiResponse(
			responseCode = "200",
			description = "좋아요 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = ResponseDto.class))
		)
	)
	ResponseDto<PagedResponse<LikeResponse>> getMyLikes(
		@Parameter(hidden = true) @CurrentUser User currentUser,
		@ParameterObject Pageable pageable
	);

	@Operation(
		summary = "게시글 좋아요",
		tags = "좋아요 기능",
		responses = @ApiResponse(
			responseCode = "201",
			description = "좋아요 추가 성공",
			content = @Content(schema = @Schema(implementation = String.class))
		)
	)
	ResponseDto<String> likePost(
		@Parameter(hidden = true) @CurrentUser User currentUser,
		@Parameter(description = "좋아요 대상 게시물 ID", example = "123") @PathVariable Long postId
	);

	@Operation(
		summary = "좋아요 취소",
		tags = "좋아요 기능",
		responses = @ApiResponse(
			responseCode = "200",
			description = "좋아요 취소 성공",
			content = @Content(schema = @Schema(implementation = String.class))
		)
	)
	ResponseDto<String> unlikePost(
		@Parameter(hidden = true) @CurrentUser User currentUser,
		@Parameter(description = "취소할 좋아요 대상 게시물 ID", example = "123") @PathVariable Long postId
	);

	@Operation(
		summary = "내가 해당 게시글에 좋아요 눌렀는지 여부 조회",
		tags = "좋아요 기능",
		responses = @ApiResponse(
			responseCode = "200",
			description = "좋아요 여부 조회 성공",
			content = @Content(schema = @Schema(implementation = Boolean.class))
		)
	)
	ResponseDto<Boolean> isPostLiked(
		@Parameter(hidden = true) @CurrentUser User currentUser,
		@Parameter(description = "조회할 게시물 ID", example = "123") @PathVariable Long postId
	);
}
