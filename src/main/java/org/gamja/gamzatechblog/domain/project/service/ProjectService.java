package org.gamja.gamzatechblog.domain.project.service;

import java.util.List;

import org.gamja.gamzatechblog.domain.project.controller.response.ProjectListResponse;
import org.gamja.gamzatechblog.domain.project.model.dto.ProjectRequest;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectService {

	List<ProjectListResponse> getAllProjects();

	ProjectListResponse createProject(User currentUser, ProjectRequest request, MultipartFile thumbnail);

	ProjectListResponse updateProject(User currentUser, Long projectId, ProjectRequest request,
		MultipartFile thumbnail);

	void deleteProject(User currentUser, Long projectId);
}
