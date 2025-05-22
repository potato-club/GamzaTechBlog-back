package org.gamja.gamzatechblog.domain.user.model.mapper;

import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
	public User toEntity(OAuthUserInfo info) {
		return User.builder()
			.githubId(info.getGithubId())
			.name(info.getName())
			.email(info.getEmail())
			.nickname(info.getNickname())
			.gamjaBatch(0)
			.build();
	}
}
