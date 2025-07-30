package org.gamja.gamzatechblog.domain.project.controller;

import java.util.List;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.project.controller.response.ProjectListResponse;
import org.gamja.gamzatechblog.domain.project.model.dto.ProjectRequest;
import org.gamja.gamzatechblog.domain.project.service.ProjectService;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@ApiController("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

	@Operation(summary = "전체 프로젝트 목록 조회", tags = "프로젝트 기능")
	@GetMapping
	public ResponseDto<List<ProjectListResponse>> getAllProjects() {
		List<ProjectListResponse> list = projectService.getAllProjects();
		return ResponseDto.of(HttpStatus.OK, "프로젝트 목록 조회 성공", list);
	}

	@Operation(summary = "프로젝트 생성", tags = "프로젝트 기능")
	@PostMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseDto<ProjectListResponse> createProject(
		@CurrentUser User user,
		@RequestPart("request") ProjectRequest request,
		@RequestPart("thumbnail") MultipartFile thumbnail
	) {
		return ResponseDto.of(
			HttpStatus.CREATED,
			"프로젝트 등록 성공",
			projectService.createProject(user, request, thumbnail)
		);
	}

	@Operation(summary = "프로젝트 수정", tags = "프로젝트 기능")
	@PutMapping(value = "/me/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseDto<ProjectListResponse> updateProject(
		@CurrentUser User user,
		@PathVariable Long projectId,
		@RequestPart("request") ProjectRequest request,
		@RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
	) {
		return ResponseDto.of(
			HttpStatus.OK,
			"프로젝트 수정 성공",
			projectService.updateProject(user, projectId, request, thumbnail)
		);
	}

	@Operation(summary = "프로젝트 삭제", tags = "프로젝트 기능")
	@DeleteMapping("/me/{projectId}")
	public ResponseDto<String> deleteProject(
		@CurrentUser User user,
		@PathVariable Long projectId
	) {
		projectService.deleteProject(user, projectId);
		return ResponseDto.of(HttpStatus.OK, "프로젝트 삭제 성공");
	}
}
