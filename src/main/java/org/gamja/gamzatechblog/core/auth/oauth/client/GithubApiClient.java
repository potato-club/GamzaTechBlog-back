package org.gamja.gamzatechblog.core.auth.oauth.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GithubApiClient {

//    @Value("${github.client.id}")
//    private final String clientId;
//
//    @Value("${github.client.secret}")
//    private final String clientSecret;
//
//    private final WebClient webClient = WebClient.builder().build();

    public Map<String, Object> fetchProfile(String code) {
//
//        // 1) code → access_token
//        String token = webClient.post()
//                .uri("https://github.com/login/oauth/access_token")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(Map.of(
//                        "client_id",     clientId,
//                        "client_secret", clientSecret,
//                        "code",          code))
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .bodyToMono(Map.class)
//                .map(m -> (String) m.get("access_token"))
//                .block();
//
//        return webClient.get()
//                .uri("https://api.github.com/user")
//                .headers(h -> h.setBearerAuth(token))
//                .retrieve()
//                .bodyToMono(Map.class)
//                .block();
        return null;
    }
}
