package org.gamja.gamzatechblog.domain.user.model.dto.request;

import org.gamja.gamzatechblog.domain.user.model.type.Position;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProfileRequest(

	@Schema(description = "이메일", example = "jihun@example.com")
	@NotBlank @Email
	String email,

	@Schema(description = "학번", example = "20231234")
	@NotBlank
	String studentNumber,

	@Schema(description = "감자 동아리 기수", example = "9")
	@NotNull
	Integer gamjaBatch,

	@Schema(description = "포지션/직책", example = "BACKEND")
	@NotNull
	Position position

) {
}
