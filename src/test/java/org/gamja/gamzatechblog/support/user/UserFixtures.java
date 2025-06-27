package org.gamja.gamzatechblog.support.user;

import org.gamja.gamzatechblog.domain.user.model.entity.User;

public final class UserFixtures {

	private UserFixtures() {
	}

	public static User user(String githubId) {
		return User.builder()
			.githubId(githubId)
			.name("User-" + githubId)
			.email(githubId + "@mail.com")
			.studentNumber("SN-" + githubId)
			.gamjaBatch(1)
			.nickname("nick-" + githubId)
			.position(null)
			.build();
	}
}
