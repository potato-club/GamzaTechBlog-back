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
		"/login/oauth2/code",
		"/v3/api-docs",
		"/swagger-ui",
		"/swagger-resources",
		"/webjars",
		"/api/v1/posts/popular",
		"/api/v1/tags",
		"/api/v1/posts/tags",
		"/jenkins",
		"/api/v1/posts/search",
		"/api/v1/intros"
	);

	public boolean shouldSkip(HttpServletRequest request) {
		String path = request.getRequestURI();
		String method = request.getMethod();
		if (path != null && path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}

		if ("GET".equalsIgnoreCase(method) && path.equals("/api/v1/projects")) {
			return true;
		}
		if ("POST".equalsIgnoreCase(method) && (path.equals("/api/admissions/lookup"))) {
			return true;
		}
		if (SKIP_PREFIXES.stream().anyMatch(path::startsWith)) {
			return true;
		}
		if ("GET".equalsIgnoreCase(method)
			&& path.startsWith("/api/v1/posts")
			&& !path.equals("/api/v1/posts/me")) {
			return true;
		}
		return false;
	}
}