package org.gamja.gamzatechblog.core.config.security;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "app.auth.github-login")
public class GithubLoginProperties {
	@NotBlank
	private String cookieDomain;

	@NotNull
	private Duration accessTokenTtl;

	@NotNull
	private Duration refreshTokenTtl;

	@NotEmpty
	private List<String> allowedLocations = new ArrayList<>();

	@NotBlank
	private String defaultLocation;

	@NotBlank
	private String redirectDomainSuffix;
}
