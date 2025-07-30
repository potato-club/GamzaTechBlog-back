package org.gamja.gamzatechblog.domain.project.service;

import org.gamja.gamzatechblog.domain.project.controller.response.ProjectListResponse;
import org.gamja.gamzatechblog.domain.project.model.dto.ProjectRequest;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectService {

	Page<ProjectListResponse> getAllProjects(Pageable pageable);

	ProjectListResponse createProject(User currentUser, ProjectRequest request, MultipartFile thumbnail);

	ProjectListResponse updateProject(User currentUser, Long projectId, ProjectRequest request,
		MultipartFile thumbnail);

	void deleteProject(User currentUser, Long projectId);
}
