package org.gamja.gamzatechblog.domain.project.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectListResponse {
	private Long id;
	private String title;
	private String snippet;
	private String thumbnailUrl;
}