package org.gamja.gamzatechblog.domain.project.infrastructure;

import org.gamja.gamzatechblog.domain.project.model.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {
}
