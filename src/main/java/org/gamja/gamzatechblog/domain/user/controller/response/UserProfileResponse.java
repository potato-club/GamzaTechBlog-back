package org.gamja.gamzatechblog.domain.user.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserProfileResponse(

	@Schema(description = "Github 아이디", example = "parkjihun")
	String githubId,

	@Schema(description = "닉네임", example = "지훈")
	String nickname,

	@Schema(description = "이름", example = "박지훈")
	String name,

	@Schema(description = "이메일", example = "jihun@example.com")
	String email,

	@Schema(description = "프로필 이미지 URL", example = "https://example-bucket.s3.amazonaws.com/profile.jpg")
	String profileImageUrl,

	@Schema(description = "직책/포지션", example = "BACKEND")
	String position,

	@Schema(description = "유저 역할", example = "USER")
	String role,

	@Schema(description = "학번", example = "20231234")
	String studentNumber,

	@Schema(description = "감자 동아리 기수", example = "9")
	Integer gamjaBatch,

	@Schema(description = "생성 일시", example = "2025-08-04T15:30:00")
	String createdAt,

	@Schema(description = "수정 일시", example = "2025-08-05T10:20:30")
	String updatedAt
) {
}
