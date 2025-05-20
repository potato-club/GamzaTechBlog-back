package org.gamja.gamzatechblog.core.auth.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GithubApiClient {

    private static final String TOKEN_URL   = "https://github.com/login/oauth/access_token";
    private static final String PROFILE_URL = "https://api.github.com/user";
    private static final String EMAILS_URL   = "https://api.github.com/user/emails";

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public String loginAndGetAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id",     clientId);
        params.add("client_secret", clientSecret);
        params.add("code",          code);
        params.add("redirect_uri",  redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                TOKEN_URL,
                HttpMethod.POST,
                request,
                JsonNode.class
        );

        JsonNode body = response.getBody();
        if (!response.getStatusCode().is2xxSuccessful() || body == null || !body.has("access_token")) {
            throw new IllegalStateException("GitHub access token 요청 실패: "
                    + response.getStatusCode() + " / body=" + body);
        }
        return body.get("access_token").asText();
    }


    public Map<String, Object> fetchProfile(String code) {
        String token = loginAndGetAccessToken(code);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                PROFILE_URL,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("GitHub 프로필 조회 실패");
        }
        return response.getBody();
    }

    public String fetchPrimaryEmail(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                EMAILS_URL,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException(
                    "GitHub 이메일 조회 실패: " + response.getStatusCode()
            );
        }

        return response.getBody().stream()
                .filter(e -> Boolean.TRUE.equals(e.get("primary"))
                        && Boolean.TRUE.equals(e.get("verified")))
                .map(e -> (String) e.get("email"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Primary 이메일을 찾을 수 없습니다."));
    }
}
