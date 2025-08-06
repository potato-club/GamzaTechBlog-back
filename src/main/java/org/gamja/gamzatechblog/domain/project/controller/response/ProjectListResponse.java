package org.gamja.gamzatechblog.domain.project.controller.response;

public record ProjectListResponse(
	Long id,
	String title,
	String snippet,
	String thumbnailUrl
) {
}
