package org.gamja.gamzatechblog.domain.project.infrastructure.jpa;

import java.util.List;
import java.util.Optional;

import org.gamja.gamzatechblog.domain.project.model.entity.Project;
import org.gamja.gamzatechblog.domain.project.service.port.ProjectRepository;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {
	private final ProjectJpaRepository projectJpaRepository;

	public List<Project> findAll() {
		return projectJpaRepository.findAll();
	}

	public Optional<Project> findById(Long id) {
		return projectJpaRepository.findById(id);
	}

	public Project save(Project p) {
		return projectJpaRepository.save(p);
	}

	public void deleteById(Long id) {
		projectJpaRepository.deleteById(id);
	}

	@Override
	public void delete(Project project) {
		projectJpaRepository.delete(project);
	}
}
