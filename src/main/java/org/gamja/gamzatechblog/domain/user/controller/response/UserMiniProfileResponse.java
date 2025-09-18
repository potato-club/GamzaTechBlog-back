package org.gamja.gamzatechblog.domain.user.controller.response;

public record UserMiniProfileResponse(
	String profileImageUrl,
	String nickname,
	Integer gamjaBatch
) {
}