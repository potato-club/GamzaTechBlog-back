package org.gamja.gamzatechblog.domain.postimage.controller;

import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.domain.postimage.service.PostImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@ApiController("/api/v1/posts/images")
@RequiredArgsConstructor
public class PostImageController {
	private final PostImageService postImageService;

	@Operation(summary = "게시글 이미지 업로드", tags = "게시글 이미지 기능")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseDto<String> uploadImage(
		@Parameter(
			description = "업로드할 이미지 파일",
			required = true,
			schema = @Schema(type = "string", format = "binary")
		)
		@RequestPart("file") MultipartFile file
	) {
		String url = postImageService.uploadImage(file);
		return ResponseDto.of(HttpStatus.CREATED, "이미지 업로드 성공", url);
	}
}
