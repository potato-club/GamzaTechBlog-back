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
			// gamjaBatch는 필요에 따라 info에 포함시키거나, 기본값 0으로 처리
			.gamjaBatch(0)
			.build();
	}
}
