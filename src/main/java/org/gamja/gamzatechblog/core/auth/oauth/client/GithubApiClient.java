package org.gamja.gamzatechblog.core.auth.oauth.client;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GithubApiClient {

	private static final String TOKEN_URL = "https://github.com/login/oauth/access_token";
	private static final String PROFILE_URL = "https://api.github.com/user";
	private static final String EMAILS_URL = "https://api.github.com/user/emails";

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
		params.add("client_id", clientId);
		params.add("client_secret", clientSecret);
		params.add("code", code);
		params.add("redirect_uri", redirectUri);

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
			new ParameterizedTypeReference<>() {
			}
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
			new ParameterizedTypeReference<>() {
			}
		);

		if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
			throw new IllegalStateException(
				"GitHub 이메일 조회 실패: " + response.getStatusCode()
			);
		}

		return response.getBody().stream()
			.filter(e -> Boolean.TRUE.equals(e.get("primary"))
				&& Boolean.TRUE.equals(e.get("verified")))
			.map(e -> (String)e.get("email"))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Primary 이메일을 찾을 수 없습니다."));
	}

	// 레포지토리 없으면 생성
	public void createRepositoryIfNotExists(String token, String repoName, String owner) {
		HttpHeaders h = new HttpHeaders();
		h.setBearerAuth(token);
		h.setAccept(List.of(MediaType.APPLICATION_JSON));

		ResponseEntity<JsonNode> resp = restTemplate.exchange("https://api.github.com/user/repos?type=owner",
			HttpMethod.GET, new HttpEntity<>(h), JsonNode.class);

		boolean exists = resp.getBody().findValues("name").stream()
			.anyMatch(n -> repoName.equals(n.asText()));

		if (!exists) {
			ObjectNode body = JsonNodeFactory.instance.objectNode();
			body.put("name", repoName);
			restTemplate.exchange(
				"https://api.github.com/user/repos", HttpMethod.POST,
				new HttpEntity<>(body, h), JsonNode.class);

			String readmeContent = """
				# %s
				
				![GamzaTechBlog](https://img.shields.io/badge/Origin-GamzaTechBlog-blueviolet?style=flat-square)
				
				이 저장소는 GamzaTechBlog 서비스에 의해 생성되었습니다.
				
				- 생성일: %s
				- 기술/프로젝트/학습 기록을 자유롭게 작성해보세요! 🚀
				
				---
				
				## ✏️ 사용 예시
				
				- 마크다운, 코드블록, 이미지 등 자유롭게 활용 가능
				- 불필요한 파일은 언제든 삭제/수정할 수 있습니다
				
				---
				
				Powered by GamzaTechBlog
				""".formatted(repoName, java.time.LocalDate.now());

			createOrUpdateFile(
				token,
				owner,
				repoName,
				"README.md",
				"Initialize repository with README",
				readmeContent
			);
		}
	}

	// 파일 생성 또는 업데이트
	public String createOrUpdateFile(String token, String owner, String repoName,
		String path, String commitMessage, String content) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		String sha = null;
		try {
			URI getUri = UriComponentsBuilder
				.fromHttpUrl("https://api.github.com/repos/{owner}/{repo}/contents/{+path}")
				.buildAndExpand(owner, repoName, path)
				.encode(StandardCharsets.UTF_8)
				.toUri();
			JsonNode resp = restTemplate.exchange(getUri, HttpMethod.GET,
				new HttpEntity<>(headers), JsonNode.class).getBody();
			sha = resp.get("sha").asText();
		} catch (HttpClientErrorException.NotFound ignored) {
		}

		ObjectNode body = JsonNodeFactory.instance.objectNode();
		body.put("message", commitMessage);
		body.put("content", Base64.getEncoder()
			.encodeToString(content.getBytes(StandardCharsets.UTF_8)));
		if (sha != null)
			body.put("sha", sha);

		URI putUri = UriComponentsBuilder
			.fromHttpUrl("https://api.github.com/repos/{owner}/{repo}/contents/{+path}")
			.buildAndExpand(owner, repoName, path)
			.encode(StandardCharsets.UTF_8)
			.toUri();
		ResponseEntity<JsonNode> putResp = restTemplate.exchange(putUri, HttpMethod.PUT,
			new HttpEntity<>(body, headers), JsonNode.class);

		return putResp.getBody().get("commit").get("sha").asText();
	}

	// 파일 삭제
	public String deleteFile(String token,
		String owner,
		String repoName,
		String path,
		String commitMessage) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		// 1) 삭제할 파일 SHA 조회
		URI getUri = UriComponentsBuilder
			.fromHttpUrl("https://api.github.com/repos/{owner}/{repo}/contents/{*path}")
			.buildAndExpand(owner, repoName, path)
			.encode(StandardCharsets.UTF_8)
			.toUri();

		ResponseEntity<JsonNode> getResp = restTemplate.exchange(
			getUri, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class);
		String sha = getResp.getBody().get("sha").asText();

		// 2) 삭제 요청 바디
		ObjectNode body = JsonNodeFactory.instance.objectNode();
		body.put("message", commitMessage);
		body.put("sha", sha);

		// 3) DELETE 요청
		URI delUri = UriComponentsBuilder
			.fromHttpUrl("https://api.github.com/repos/{owner}/{repo}/contents/{*path}")
			.buildAndExpand(owner, repoName, path)
			.encode(StandardCharsets.UTF_8)
			.toUri();

		ResponseEntity<JsonNode> delResp = restTemplate.exchange(delUri, HttpMethod.DELETE,
			new HttpEntity<>(body, headers), JsonNode.class);

		return delResp.getBody().get("commit").get("sha").asText();
	}
}
