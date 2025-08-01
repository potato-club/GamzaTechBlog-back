package org.gamja.gamzatechblog.domain.project.service.impl;

import java.util.List;

import org.gamja.gamzatechblog.common.port.s3.S3ImageStorage;
import org.gamja.gamzatechblog.domain.project.controller.response.ProjectListResponse;
import org.gamja.gamzatechblog.domain.project.model.dto.ProjectRequest;
import org.gamja.gamzatechblog.domain.project.model.entity.Project;
import org.gamja.gamzatechblog.domain.project.model.mapper.ProjectMapper;
import org.gamja.gamzatechblog.domain.project.service.ProjectCacheFacade;
import org.gamja.gamzatechblog.domain.project.service.ProjectService;
import org.gamja.gamzatechblog.domain.project.service.port.ProjectRepository;
import org.gamja.gamzatechblog.domain.project.validator.ProjectValidator;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

	private static final String PREFIX = "project_images";

	private final ProjectRepository projectRepository;
	private final ProjectValidator projectValidator;
	private final S3ImageStorage s3ImageStorage;
	private final ProjectMapper projectMapper;
	private final ProjectCacheFacade projectCacheFacade;

	@Override
	@Transactional(readOnly = true)
	public Page<ProjectListResponse> getAllProjects(Pageable pageable) {
		List<ProjectListResponse> content = projectCacheFacade.getPagedContent(pageable);
		long total = projectCacheFacade.getTotal();
		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public ProjectListResponse createProject(User currentUser, ProjectRequest request, MultipartFile thumbnail) {

		projectValidator.validateThumbnail(thumbnail);
		Project project = projectMapper.toProject(request, currentUser, null);
		projectRepository.save(project);
		String thumbnailUrl = s3ImageStorage.uploadFile(thumbnail, PREFIX);
		project.change(project.getTitle(), project.getDescription(), thumbnailUrl);
		return projectMapper.toListResponse(project);
	}

	@Override
	public ProjectListResponse updateProject(User currentUser,
		Long projectId,
		ProjectRequest request,
		MultipartFile thumbnail) {

		Project project = projectValidator.validateAndGetProject(projectId);
		projectValidator.validateOwnership(project, currentUser);

		String thumbnailUrl = project.getThumbnailUrl();

		if (thumbnail != null && !thumbnail.isEmpty()) {
			projectValidator.validateThumbnail(thumbnail);
			thumbnailUrl = s3ImageStorage.uploadFile(thumbnail, PREFIX);

			if (project.getThumbnailUrl() != null) {
				s3ImageStorage.deleteByUrl(project.getThumbnailUrl());
			}
		}

		project.change(request.getTitle(), request.getDescription(), thumbnailUrl);
		return projectMapper.toListResponse(project);
	}

	@Override
	public void deleteProject(User currentUser, Long projectId) {

		Project project = projectValidator.validateAndGetProject(projectId);
		projectValidator.validateOwnership(project, currentUser);

		if (project.getThumbnailUrl() != null) {
			s3ImageStorage.deleteByUrl(project.getThumbnailUrl());
		}
		projectRepository.delete(project);
	}
}
