package org.gamja.gamzatechblog.domain.tag.controller.docs;

import java.util.List;

import org.gamja.gamzatechblog.common.dto.ResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

public interface TagControllerSwagger {

	@Operation(
		summary = "전체 태그 조회",
		tags = "태그 기능",
		responses = @ApiResponse(
			responseCode = "200",
			description = "태그 목록 조회 성공",
			content = @Content(
				array = @ArraySchema(schema = @Schema(implementation = String.class))
			)
		)
	)
	ResponseDto<List<String>> getAllTags();
}
