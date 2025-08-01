package org.gamja.gamzatechblog.domain.project.service;

import java.util.List;

import org.gamja.gamzatechblog.domain.project.controller.response.ProjectListResponse;
import org.gamja.gamzatechblog.domain.project.service.port.ProjectQueryPort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectCacheFacade {

	private final ProjectQueryPort projectQueryPort;

	@Cacheable(
		value = "projectsListContent",
		key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()",
		unless = "#result.isEmpty()"
	)
	@Transactional(readOnly = true)
	public List<ProjectListResponse> getPagedContent(Pageable pageable) {
		return projectQueryPort.findAllProjects(pageable).getContent();
	}

	@Cacheable(value = "projectsTotal", key = "'all'")
	@Transactional(readOnly = true)
	public long getTotal() {
		return projectQueryPort.countAllProjects();
	}
}
