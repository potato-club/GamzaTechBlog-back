package org.gamja.gamzatechblog.domain.postimage.controller;

import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.postimage.service.impl.PostImageService;

import lombok.RequiredArgsConstructor;

@ApiController("/api/v1/posts/images")
@RequiredArgsConstructor
public class PostImageController {
	private final PostImageService postImageService;

	// @Operation(summary = "게시글 이미지 업로드", tags = "게시글 이미지 기능")
	// @PostMapping
	// public ResponseDto<String> upload(@RequestPart("file") MultipartFile file) {
	// 	String url = postImageService.uploadImage(file);
	// 	return ResponseDto.of(HttpStatus.CREATED, "이미지 업로드 성공", url);
	// } 지금은 필요없음
}
