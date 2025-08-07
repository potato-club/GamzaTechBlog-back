// package org.gamja.gamzatechblog.domain.user.controller.docs;
//
// import org.gamja.gamzatechblog.common.dto.ResponseDto;
// import org.gamja.gamzatechblog.domain.user.controller.response.UserActivityResponse;
// import org.gamja.gamzatechblog.domain.user.controller.response.UserProfileResponse;
// import org.gamja.gamzatechblog.domain.user.model.dto.request.UpdateProfileRequest;
// import org.gamja.gamzatechblog.domain.user.model.dto.request.UserProfileRequest;
// import org.gamja.gamzatechblog.domain.user.model.entity.User;
//
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.parameters.RequestBody;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//
// @SecurityRequirement(name = "bearerAuth")
// public interface UserControllerSwagger {
//
// 	@Operation(
// 		summary = "정보 조회",
// 		tags = "유저 기능",
// 		responses = @ApiResponse(
// 			responseCode = "200",
// 			description = "프로필 조회 성공",
// 			content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
// 		)
// 	)
// 	ResponseDto<UserProfileResponse> getCurrentUserProfile(
// 		@Parameter(hidden = true) User currentUser
// 	);
//
// 	@Operation(
// 		summary = "정보 업데이트(마이페이지용)",
// 		tags = "유저 기능",
// 		requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UpdateProfileRequest.class))),
// 		responses = @ApiResponse(
// 			responseCode = "200",
// 			description = "프로필이 수정되었습니다",
// 			content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
// 		)
// 	)
// 	ResponseDto<UserProfileResponse> updateProfile(
// 		@Parameter(hidden = true) User currentUser,
// 		UpdateProfileRequest request
// 	);
//
// 	@Operation(
// 		summary = "계정 삭제",
// 		tags = "유저 기능",
// 		responses = @ApiResponse(
// 			responseCode = "200",
// 			description = "삭제되었습니다",
// 			content = @Content(schema = @Schema(implementation = String.class))
// 		)
// 	)
// 	ResponseDto<String> withdraw(
// 		@Parameter(hidden = true) User currentUser
// 	);
//
// 	@Operation(
// 		summary = "추가 정보 입력(회원가입용)",
// 		tags = "유저 기능",
// 		requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = UserProfileRequest.class))),
// 		responses = @ApiResponse(
// 			responseCode = "200",
// 			description = "프로필이 성공적으로 완성되었습니다.",
// 			content = @Content(schema = @Schema(implementation = UserProfileResponse.class))
// 		)
// 	)
// 	ResponseDto<UserProfileResponse> completeProfile(
// 		UserProfileRequest userProfileRequest,
// 		@Parameter(hidden = true) User currentUser
// 	);
//
// 	@Operation(
// 		summary = "유저 활동(내가 쓴 글/댓글/좋아요 개수) 조회",
// 		tags = "유저 기능",
// 		responses = @ApiResponse(
// 			responseCode = "200",
// 			description = "유저 활동 정보 조회 성공",
// 			content = @Content(schema = @Schema(implementation = UserActivityResponse.class))
// 		)
// 	)
// 	ResponseDto<UserActivityResponse> getActivitySummary(
// 		@Parameter(hidden = true) User currentUser
// 	);
//
// 	@Operation(
// 		summary = "역할 조회",
// 		tags = "유저 기능",
// 		responses = @ApiResponse(
// 			responseCode = "200",
// 			description = "역할 조회 성공",
// 			content = @Content(schema = @Schema(implementation = String.class))
// 		)
// 	)
// 	ResponseDto<String> getCurrentUserRole(
// 		@Parameter(hidden = true) User currentUser
// 	);
// }
