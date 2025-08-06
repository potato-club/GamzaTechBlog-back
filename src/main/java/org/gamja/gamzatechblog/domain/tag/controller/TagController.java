package org.gamja.gamzatechblog.domain.tag.controller;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.tag.controller.docs.TagControllerSwagger;
import org.gamja.gamzatechblog.domain.tag.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@ApiController("/api/v1/tags")
@RequiredArgsConstructor
public class TagController implements TagControllerSwagger {
	private final TagService tagService;

	@GetMapping
	public ResponseDto<List<String>> getAllTags() {
		List<String> tags = tagService.getAllTags();
		return ResponseDto.of(HttpStatus.OK, "태그 목록 조회 성공", tags);
	}
}
