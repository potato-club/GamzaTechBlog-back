package org.gamja.gamzatechblog.domain.introduction;

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

