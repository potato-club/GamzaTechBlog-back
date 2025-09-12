package org.gamja.gamzatechblog.core.config.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "gemini.api")
public class GeminiProperties {
	private String baseUrl;
	private String model;
	private String key;
}
