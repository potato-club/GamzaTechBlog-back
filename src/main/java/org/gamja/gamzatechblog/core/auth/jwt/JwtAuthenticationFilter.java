package org.gamja.gamzatechblog.core.auth.jwt;

import java.io.IOException;

import org.gamja.gamzatechblog.core.auth.jwt.validator.JwtTokenValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtProvider jwtProvider;
	private final JwtSkipPathMatcher skipPathMatcher;
	private final JwtTokenValidator tokenValidator;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		if (skipPathMatcher.shouldSkip(request)) {
			filterChain.doFilter(request, response);
			return;
		}
		String token = tokenValidator.resolveAndValidate(request);
		Authentication auth = jwtProvider.getAuthentication(token);
		SecurityContextHolder.getContext().setAuthentication(auth);

		filterChain.doFilter(request, response);
	}
}
