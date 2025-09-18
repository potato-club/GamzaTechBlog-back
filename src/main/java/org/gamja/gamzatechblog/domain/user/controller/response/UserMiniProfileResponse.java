package org.gamja.gamzatechblog.domain.user.controller.response;

public record UserMiniProfileResponse(
	String nickname,
	String gamjaBatch,
	String email
) {
}