package org.gamja.gamzatechblog.domain.project.infrastructure.adapter;

import static org.gamja.gamzatechblog.domain.project.model.entity.QProject.*;

import java.util.List;

import org.gamja.gamzatechblog.domain.project.controller.response.ProjectListResponse;
import org.gamja.gamzatechblog.domain.project.service.port.ProjectQueryPort;
import org.gamja.gamzatechblog.domain.project.util.ProjectUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProjectQueryAdapter implements ProjectQueryPort {

	private final JPAQueryFactory queryFactory;
	private final ProjectUtil projectUtil;

	@Override
	public Page<ProjectListResponse> findAllProjects(Pageable pageable) {
		List<Tuple> tuples = queryFactory
			.select(project.id, project.title, project.description,
				project.thumbnailUrl, project.duration, project.createdAt)
			.from(project)
			.orderBy(project.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		List<ProjectListResponse> content = tuples.stream()
			.map(t -> ProjectListResponse.builder()
				.id(t.get(project.id))
				.title(t.get(project.title))
				.snippet(projectUtil.makeSnippet(t.get(project.description), 100))
				.thumbnailUrl(t.get(project.thumbnailUrl))
				.build())
			.toList();

		long totalCount = countAllProjects();
		return new PageImpl<>(content, pageable, totalCount);
	}

	@Override
	public long countAllProjects() {
		return queryFactory
			.select(project.count())
			.from(project)
			.fetchOne();
	}
}
