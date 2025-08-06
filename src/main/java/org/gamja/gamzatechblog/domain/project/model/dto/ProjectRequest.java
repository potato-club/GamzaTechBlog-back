package org.gamja.gamzatechblog.domain.project.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 생성/수정 요청 DTO")
public record ProjectRequest(
	@Schema(description = "프로젝트 제목", example = "Gamza Tech Blog 리뉴얼")
	String title,

	@Schema(description = "프로젝트 설명", example = "Gradle, Docker, Jenkins 를 활용한 블로그 플랫폼 개발")
	String description
) {
}
