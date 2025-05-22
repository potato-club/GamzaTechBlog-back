package org.gamja.gamzatechblog.core.auth.oauth.model;

import java.util.Map;

public class GithubUser implements OAuthUserInfo {

	public static final String[] GITHUB_OAUTH_SCOPES = {
		"read:user",
		"user:email",
		"repo"
	};

	private final Map<String, Object> attributes;

	private String email;

	public GithubUser(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getGithubId() {
		return "g_" + attributes.get("id");
	}

	@Override
	public String getProvider() {
		return OAuthProvider.GITHUB;
	}

	@Override
	public String getNickname() {
		return (String)attributes.get("login");
	}

	@Override
	public String getEmail() {
		return (String)attributes.get("email");
	}

	@Override
	public String getName() {
		Object name = attributes.get("name");
		return name != null ? name.toString() : (String)attributes.get("login");
	}

	@Override
	public String getProfileImageUrl() {
		return (String)attributes.get("avatar_url");
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
