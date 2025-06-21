package org.gamja.gamzatechblog.domain.user.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
	private final String githubId;
	private final String nickname;
	private final String name;
	private final String email;
	private final String profileImageUrl;
	private final String role;
	private final Integer gamjaBatch;
	private final String createdAt;
	private final String updatedAt;
}
