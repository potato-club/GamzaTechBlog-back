package org.gamja.gamzatechblog.domain.profileimage.model.dto.request;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProfileImageRequest(
	@Schema(
		description = "업로드할 프로필 이미지 파일",
		type = "string",
		format = "binary",
		requiredMode = Schema.RequiredMode.REQUIRED
	)
	@NotNull(message = "이미지 파일은 필수입니다")
	MultipartFile imageFile
) {
}
