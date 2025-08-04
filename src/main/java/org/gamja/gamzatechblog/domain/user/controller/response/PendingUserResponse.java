package org.gamja.gamzatechblog.domain.user.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PendingUserResponse(

	@Schema(description = "유저 ID", example = "12")
	Long userId,

	@Schema(description = "감자기수", example = "9")
	Integer gamjaBatch,

	@Schema(description = "이름", example = "박지훈")
	String name,

	@Schema(description = "포지션/직책", example = "BACKEND")
	String position
) {
}
