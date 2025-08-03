package org.gamja.gamzatechblog.domain.user.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
	private String githubId;
	private String nickname;
	private String name;
	private String email;
	private String profileImageUrl;
	private String position;
	private String role;
	private String studentNumber;
	private Integer gamjaBatch;
	private String createdAt;
	private String updatedAt;
}
