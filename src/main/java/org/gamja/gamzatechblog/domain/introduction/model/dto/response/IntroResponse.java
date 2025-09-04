package org.gamja.gamzatechblog.domain.introduction.model.dto.response;

import java.time.LocalDateTime;

public record IntroResponse(
	Long introId,
	Long userId,
	String nickname,
	String profileImageUrl,
	String content,
	LocalDateTime createdAt
) {
}

