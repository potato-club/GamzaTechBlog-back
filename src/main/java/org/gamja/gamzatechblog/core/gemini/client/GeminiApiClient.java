package org.gamja.gamzatechblog.core.gemini.client;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import org.gamja.gamzatechblog.core.config.ai.GeminiProperties;
import org.gamja.gamzatechblog.core.gemini.dto.gemini.request.GeminiRequest;
import org.gamja.gamzatechblog.core.gemini.dto.gemini.response.GeminiResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GeminiApiClient {
	/**
	 * 관련 코드 모두 리팩토링 예정입니다.
	 */

	private static final String PATH_TEMPLATE = "/models/%s:generateContent";

	private final RestTemplate restTemplate;
	private final GeminiProperties properties;

	public GeminiApiClient(RestTemplate restTemplate, GeminiProperties properties) {
		this.restTemplate = restTemplate;
		this.properties = properties;
	}

	public String generateTextWithSystem(String systemInstruction, String userText) {
		URI uri = UriComponentsBuilder
			.fromHttpUrl(properties.getBaseUrl())
			.path(String.format(PATH_TEMPLATE, properties.getModel()))
			.queryParam("key", properties.getKey())
			.build()
			.toUri();

		GeminiRequest body = GeminiRequest.fromSystemAndUser(systemInstruction, userText);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));

		ResponseEntity<GeminiResponse> response;
		try {
			response = restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(body, headers),
				GeminiResponse.class);
		} catch (HttpStatusCodeException e) {
			String err = e.getResponseBodyAsString(StandardCharsets.UTF_8);
			throw new IllegalStateException("Gemini API 호출 실패: HTTP " + e.getStatusCode() + " / " + err);
		}

		GeminiResponse res = response.getBody();
		if (Objects.isNull(res) || res.getCandidates() == null || res.getCandidates().isEmpty()) {
			throw new IllegalStateException("Gemini API 응답이 비어 있습니다.");
		}
		GeminiResponse.Candidate cand = res.getCandidates().get(0);
		if (cand.getContent() == null || cand.getContent().getParts() == null || cand.getContent()
			.getParts()
			.isEmpty()) {
			throw new IllegalStateException("Gemini API 응답 파싱 실패: parts 없음");
		}
		String text = cand.getContent().getParts().get(0).getText();
		if (text == null || text.isBlank()) {
			throw new IllegalStateException("Gemini API 응답 파싱 실패: text 없음");
		}
		return text.trim();
	}
}
