package org.gamja.gamzatechblog.domain.project.controller.docs;

import java.util.List;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.domain.project.controller.response.ProjectListResponse;
import org.gamja.gamzatechblog.domain.project.model.dto.ProjectRequest;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
public interface ProjectControllerSwagger {

	@Operation(
		summary = "전체 프로젝트 목록 조회",
		tags = "프로젝트 기능",
		responses = @ApiResponse(
			responseCode = "200",
			description = "프로젝트 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = ProjectListResponse.class))
		)
	)
	ResponseDto<List<ProjectListResponse>> getAllProjects(
		@ParameterObject Pageable pageable
	);

	@Operation(
		summary = "프로젝트 생성",
		tags = "프로젝트 기능",
		requestBody = @RequestBody(
			description = "프로젝트 정보와 썸네일 파일",
			content = @Content(
				mediaType = "multipart/form-data",
				schema = @Schema(implementation = ProjectRequest.class)
			)
		),
		responses = @ApiResponse(
			responseCode = "201",
			description = "프로젝트 등록 성공",
			content = @Content(schema = @Schema(implementation = ProjectListResponse.class))
		)
	)
	ResponseDto<ProjectListResponse> createProject(
		@Parameter(hidden = true) @CurrentUser User user,
		ProjectRequest request,
		@Parameter(description = "썸네일 이미지 파일", content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary")))
		MultipartFile thumbnail
	);

	@Operation(
		summary = "프로젝트 수정",
		tags = "프로젝트 기능",
		requestBody = @RequestBody(
			description = "수정할 프로젝트 정보와 선택적 썸네일",
			content = @Content(
				mediaType = "multipart/form-data",
				schema = @Schema(implementation = ProjectRequest.class)
			)
		),
		responses = @ApiResponse(
			responseCode = "200",
			description = "프로젝트 수정 성공",
			content = @Content(schema = @Schema(implementation = ProjectListResponse.class))
		)
	)
	ResponseDto<ProjectListResponse> updateProject(
		@Parameter(hidden = true) @CurrentUser User user,
		@Parameter(description = "수정할 프로젝트 ID", example = "1") Long projectId,
		ProjectRequest request,
		@Parameter(description = "새 썸네일 이미지 파일 (선택)", content = @Content(mediaType = "multipart/form-data", schema = @Schema(type = "string", format = "binary")))
		MultipartFile thumbnail
	);

	@Operation(
		summary = "프로젝트 삭제",
		tags = "프로젝트 기능",
		responses = @ApiResponse(
			responseCode = "200",
			description = "프로젝트 삭제 성공",
			content = @Content(schema = @Schema(implementation = String.class))
		)
	)
	ResponseDto<String> deleteProject(
		@Parameter(hidden = true) @CurrentUser User user,
		@Parameter(description = "삭제할 프로젝트 ID", example = "1") Long projectId
	);
}
