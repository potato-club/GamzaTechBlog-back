package org.gamja.gamzatechblog.domain.project.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProjectListResponse {
	private Long id;
	private String title;
	private String snippet;
	private String thumbnailUrl;
}