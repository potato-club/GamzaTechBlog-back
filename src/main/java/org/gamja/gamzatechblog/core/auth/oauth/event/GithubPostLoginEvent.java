package org.gamja.gamzatechblog.core.auth.oauth.event;

public record GithubPostLoginEvent(String githubId, String accessToken) {
}
