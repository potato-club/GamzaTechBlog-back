package org.gamja.gamzatechblog.core.auth.oauth.model;

// after
public interface OAuthUserInfo {
	String getGithubId();

	String getProvider();

	String getEmail();

	String getNickname();

	String getName();

	String getProfileImageUrl();  // 이름 변경

}


