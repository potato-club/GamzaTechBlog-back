package org.gamja.gamzatechblog.core.config.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "gamza.chatbot")
public class ChatbotProperties {
	private long cacheTtlSeconds;
	private int rateLimitMaxRequests;
	private long rateLimitWindowSeconds;

	private String system;
	private String blogBrief;
}
