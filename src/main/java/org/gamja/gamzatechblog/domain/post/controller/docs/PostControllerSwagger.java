package org.gamja.gamzatechblog.domain.post.controller.docs;

import java.util.List;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.PagedResponse;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.domain.post.model.dto.request.PostRequest;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostDetailResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostListResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostPopularResponse;
import org.gamja.gamzatechblog.domain.post.model.dto.response.PostResponse;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotBlank;

@SecurityRequirement(name = "bearerAuth")
public interface PostControllerSwagger {

	@Operation(
		summary = "게시물 작성",
		tags = "게시물 기능",
		responses = @ApiResponse(
			responseCode = "201",
			description = "게시물 작성 성공",
			content = @Content(schema = @Schema(implementation = PostResponse.class))
		)
	)
	ResponseDto<PostResponse> publishPost(
		@Parameter(hidden = true) @CurrentUser User currentUser,
		@RequestBody(
			description = "게시물 작성 정보",
			required = true,
			content = @Content(schema = @Schema(implementation = PostRequest.class))
		)
		PostRequest request
	);

	@Operation(
		summary = "게시물 수정",
		tags = "게시물 기능",
		responses = @ApiResponse(
			responseCode = "200",
			description = "게시물 수정 성공",
			content = @Content(schema = @Schema(implementation = PostResponse.class))
		)
	)
	ResponseDto<PostResponse> revisePost(
		@Parameter(hidden = true) @CurrentUser User currentUser,
		@Parameter(description = "수정할 게시물 ID", example = "123") Long id,
		@RequestBody(
			description = "수정할 내용",
			required = true,
			content = @Content(schema = @Schema(implementation = PostRequest.class))
		)
		PostRequest request
	);

	@Operation(
		summary = "게시물 삭제",
		tags = "게시물 기능",
		responses = @ApiResponse(
			responseCode = "204",
			description = "게시물 삭제 성공",
			content = @Content(schema = @Schema(implementation = Void.class))
		)
	)
	ResponseDto<Void> removePost(
		@Parameter(hidden = true) @CurrentUser User currentUser,
		@Parameter(description = "삭제할 게시물 ID", example = "123") Long id
	);

	@Operation(
		summary = "최신순 게시물 목록 조회",
		tags = "게시물 조회 기능",
		responses = @ApiResponse(
			responseCode = "200",
			description = "게시물 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = PagedResponse.class))
		)
	)
	ResponseDto<PagedResponse<PostListResponse>> getPosts(
		@ParameterObject Pageable pageable,
		@Parameter(description = "필터 태그 리스트", example = "[\"java\",\"spring\"]") List<String> filterTags
	);

	@Operation(
		summary = "단일 게시물 상세 조회",
		tags = "게시물 조회 기능",
		responses = @ApiResponse(
			responseCode = "200",
			description = "게시물 상세 조회 성공",
			content = @Content(schema = @Schema(implementation = PostDetailResponse.class))
		)
	)
	ResponseDto<PostDetailResponse> getPostDetail(
		@Parameter(description = "조회할 게시물 ID", example = "123") Long postId
	);

	@Operation(
		summary = "내가 쓴 게시물 목록 조회",
		tags = "게시물 조회 기능",
		responses = @ApiResponse(
			responseCode = "200",
			description = "내가 쓴 게시물 조회 성공",
			content = @Content(schema = @Schema(implementation = PagedResponse.class))
		)
	)
	ResponseDto<PagedResponse<PostListResponse>> getMyPosts(
		@Parameter(hidden = true) @CurrentUser User currentUser,
		@ParameterObject Pageable pageable
	);

	@Operation(
		summary = "주간 인기 게시물 조회",
		tags = "게시물 조회 기능",
		responses = @ApiResponse(
			responseCode = "200",
			description = "주간 인기 게시물 조회 성공",
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = PostPopularResponse.class)))
		)
	)
	ResponseDto<List<PostPopularResponse>> getWeeklyPopularPosts();

	@Operation(
		summary = "태그별 게시물 조회",
		tags = "게시물 조회 기능",
		responses = @ApiResponse(
			responseCode = "200",
			description = "태그별 게시물 조회 성공",
			content = @Content(schema = @Schema(implementation = PagedResponse.class))
		)
	)
	ResponseDto<PagedResponse<PostListResponse>> getPostsByTag(
		@Parameter(description = "태그 이름", example = "spring") String tagName,
		@ParameterObject Pageable pageable
	);

	@Operation(
		summary = "게시물 검색",
		tags = "게시물 조회 기능",
		responses = @ApiResponse(
			responseCode = "200",
			description = "게시물 검색 조회 성공",
			content = @Content(schema = @Schema(implementation = PagedResponse.class))
		)
	)
	ResponseDto<PagedResponse<PostListResponse>> searchPosts(
		@ParameterObject Pageable pageable,
		@Parameter(description = "검색 키워드", example = "Redis")
		@RequestParam("keyword")
		@NotBlank(message = "검색 키워드는 필수입니다") String keyword
	);
}
