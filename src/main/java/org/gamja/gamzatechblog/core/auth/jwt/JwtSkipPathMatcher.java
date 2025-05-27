package org.gamja.gamzatechblog.core.auth.jwt;

import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

/*
이 부분은 인증 인가에서 인증 부분입니다.
 */
@Component
public class JwtSkipPathMatcher {
	private static final List<String> SKIP_PREFIXES = List.of(
		"/api/auth/reissue",
		"/oauth2",
		"/v3/api-docs",
		"/swagger-ui",
		"/swagger-resources",
		"/webjars",
		"/api/v1/tags"
	);

	public boolean shouldSkip(HttpServletRequest request) {
		String path = request.getRequestURI();
		return SKIP_PREFIXES.stream().anyMatch(path::startsWith);
	}
}
