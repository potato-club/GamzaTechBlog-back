package org.gamja.gamzatechblog.support.intro;

import org.gamja.gamzatechblog.domain.introduction.model.entity.Introduction;
import org.gamja.gamzatechblog.domain.user.model.entity.User;

public final class IntroFixtures {

	private IntroFixtures() {
	}

	public static String content(String seed) {
		return "소개글 내용 - " + seed;
	}

	public static Introduction introduction(User user, String seed) {
		return Introduction.builder()
			.user(user)
			.content(content(seed))
			.build();
	}
}
