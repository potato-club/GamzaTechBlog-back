package org.gamja.gamzatechblog.core.auth.oauth.mapper;

import lombok.RequiredArgsConstructor;
import org.gamja.gamzatechblog.core.auth.oauth.client.GithubApiClient;
import org.gamja.gamzatechblog.core.auth.oauth.model.GithubUser;
import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GithubUserMapper {

    private final GithubApiClient githubApiClient;

    public OAuthUserInfo toOAuthUserInfo(String code) {
        String token = githubApiClient.loginAndGetAccessToken(code);

        Map<String, Object> profile = githubApiClient.fetchProfile(code);

        GithubUser user = new GithubUser(profile);
        if (user.getEmail() == null) {
            String primary = githubApiClient.fetchPrimaryEmail(token);
            user.setEmail(primary);
        }

        return user;
    }
}
