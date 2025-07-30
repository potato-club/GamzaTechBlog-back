package org.gamja.gamzatechblog.domain.project.service.port;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.project.model.entity.Project;

public interface ProjectRepository {
	List<Project> findAll();

	Optional<Project> findById(Long id);

	Project save(Project project);

	void deleteById(Long id);

	void delete(Project project);
}
