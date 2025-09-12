package org.gamja.gamzatechblog.core.gemini.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;

import org.gamja.gamzatechblog.core.config.ai.ChatbotProperties;
import org.gamja.gamzatechblog.core.gemini.client.GeminiApiClient;
import org.gamja.gamzatechblog.core.gemini.dto.chat.request.ChatMessageRequest;
import org.gamja.gamzatechblog.core.gemini.dto.chat.response.ChatMessageResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class GamzaChatService {

	private static final String CACHE_PREFIX = "gzb:chat:cache:v1:";

	private final GeminiApiClient geminiApiClient;
	private final ChatbotProperties chatbotProperties;
	private final StringRedisTemplate cacheStringRedisTemplate;

	public GamzaChatService(
		GeminiApiClient geminiApiClient,
		ChatbotProperties chatbotProperties,
		@Qualifier("cacheStringRedisTemplate") StringRedisTemplate cacheStringRedisTemplate
	) {
		this.geminiApiClient = geminiApiClient;
		this.chatbotProperties = chatbotProperties;
		this.cacheStringRedisTemplate = cacheStringRedisTemplate;
	}

	public ChatMessageResponse getReply(ChatMessageRequest request) {
		// 방어적 가드(컨트롤러 @Valid와 중복이지만 비용 보호용)
		String userMsg = request.getMessage();
		if (userMsg == null || userMsg.isBlank()) {
			throw new IllegalArgumentException("메시지는 비어 있을 수 없습니다.");
		}
		if (userMsg.length() > 2000) {
			throw new IllegalArgumentException("길이가 너무 깁니다.");
		}
		userMsg = userMsg.strip();

		String system = buildSystemInstruction();
		String user = buildUserMessage(userMsg);

		long ttlSeconds = chatbotProperties.getCacheTtlSeconds();
		if (ttlSeconds > 0) {
			String cacheKey = buildCacheKey(system + "\n---\n" + user);
			String cached = readCache(cacheKey);
			if (cached != null && !cached.isBlank()) {
				return new ChatMessageResponse(cached);
			}
			String generated = geminiApiClient.generateTextWithSystem(system, user);
			writeCache(cacheKey, generated, Duration.ofSeconds(ttlSeconds));
			return new ChatMessageResponse(generated);
		}

		String generated = geminiApiClient.generateTextWithSystem(system, user);
		return new ChatMessageResponse(generated);
	}

	private String buildSystemInstruction() {
		return chatbotProperties.getSystem();
	}

	private String buildUserMessage(String userMessage) {
		return """
			아래는 GamzaTechBlog 소개 및 기능 요약이다. 이를 근거로 질문에 답해라.
			불필요한 소개 반복은 피하고, 모르면 모른다고 답하라.
			
			[블로그 요약]
			%s
			
			[사용자 질문]
			%s
			"""
			.formatted(chatbotProperties.getBlogBrief(), userMessage);
	}

	private String buildCacheKey(String text) {
		return CACHE_PREFIX + sha256Base64(text);
	}

	private String readCache(String key) {
		try {
			return cacheStringRedisTemplate.opsForValue().get(key);
		} catch (DataAccessException e) {
			return null;
		}
	}

	private void writeCache(String key, String value, Duration ttl) {
		try {
			cacheStringRedisTemplate.opsForValue().set(key, value, ttl);
		} catch (DataAccessException e) {
		}
	}

	private String sha256Base64(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
			return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
		} catch (Exception e) {
			return Integer.toHexString(input.hashCode());
		}
	}
}
