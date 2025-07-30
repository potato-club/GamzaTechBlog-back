package org.gamja.gamzatechblog.domain.project.validator;

import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.domain.project.exception.ProjectImageEmptyException;
import org.gamja.gamzatechblog.domain.project.exception.ProjectNotFoundException;
import org.gamja.gamzatechblog.domain.project.exception.ProjectNotOwnerException;
import org.gamja.gamzatechblog.domain.project.model.entity.Project;
import org.gamja.gamzatechblog.domain.project.service.port.ProjectRepository;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectValidator {

	private final ProjectRepository projectRepository;

	public Project validateAndGetProject(Long projectId) {
		return projectRepository.findById(projectId)
			.orElseThrow(() -> new ProjectNotFoundException(ErrorCode.PROJECT_NOT_FOUND));
	}

	public void validateOwnership(Project project, User currentUser) {
		if (!project.getOwner().getId().equals(currentUser.getId())) {
			throw new ProjectNotOwnerException(ErrorCode.PROJECT_NOT_OWNER);
		}
	}

	public void validateThumbnail(MultipartFile thumbnail) {
		if (thumbnail == null || thumbnail.isEmpty()) {
			throw new ProjectImageEmptyException(
				ErrorCode.PROJECT_IMAGE_EMPTY);
		}
	}
}
