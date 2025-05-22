package org.gamja.gamzatechblog.domain.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileDto {
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
