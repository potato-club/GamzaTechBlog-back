package org.gamja.gamzatechblog.core.auth.oauth.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GithubApiClient {

    public Map<String, Object> fetchProfile(String code) {

        return null;
    }
}
