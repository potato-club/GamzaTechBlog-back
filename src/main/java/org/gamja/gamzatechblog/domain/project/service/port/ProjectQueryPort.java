package org.gamja.gamzatechblog.domain.project.service.port;

import org.gamja.gamzatechblog.domain.project.controller.response.ProjectListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectQueryPort {
	Page<ProjectListResponse> findAllProjects(Pageable pageable);

	long countAllProjects();
}
