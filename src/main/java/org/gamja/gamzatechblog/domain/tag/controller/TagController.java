package org.gamja.gamzatechblog.domain.tag.controller;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.tag.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@ApiController("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {
	private final TagService tagService;

	@Operation(summary = "전체 태그 조회", tags = "태그 기능")
	@GetMapping
	public ResponseDto<List<String>> getAllTags() {
		List<String> tags = tagService.getAllTags();
		return ResponseDto.of(HttpStatus.OK, "태그 목록 조회 성공", tags);
	}
}
