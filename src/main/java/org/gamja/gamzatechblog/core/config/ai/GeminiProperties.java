package org.gamja.gamzatechblog.core.config.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "key")
@Component
@ConfigurationProperties(prefix = "gemini.api")
@Validated
public class GeminiProperties {
	@NotBlank
	private String baseUrl;

	@NotBlank
	private String model;

	@NotBlank
	private String key;
}
