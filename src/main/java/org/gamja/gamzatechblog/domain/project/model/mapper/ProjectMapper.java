package org.gamja.gamzatechblog.domain.project.model.mapper;

import org.gamja.gamzatechblog.domain.project.controller.response.ProjectListResponse;
import org.gamja.gamzatechblog.domain.project.model.dto.ProjectRequest;
import org.gamja.gamzatechblog.domain.project.model.entity.Project;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

	@Mapping(target = "snippet", source = "description",
		qualifiedByName = "generateDescriptionSnippet")
	ProjectListResponse toListResponse(Project project);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "title", source = "dto.title")
	@Mapping(target = "description", source = "dto.description")
	@Mapping(target = "owner", source = "owner")
	@Mapping(target = "thumbnailUrl", source = "thumbnailUrl")
	@Mapping(target = "duration", ignore = true)
	Project toProject(ProjectRequest dto, User owner, String thumbnailUrl);

	@Named("generateDescriptionSnippet")
	static String generateDescriptionSnippet(String description) {
		if (description == null || description.isBlank())
			return "";
		return description.length() > 80 ? description.substring(0, 80) + "…" : description;
	}
}
