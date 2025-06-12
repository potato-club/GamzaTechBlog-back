package org.gamja.gamzatechblog.core.auth.oauth.util;

import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieUtils {

	public void addAccessTokenCookie(HttpServletResponse res,
		String accessToken,
		String domain,
		Duration maxAge) {
		ResponseCookie cookie = ResponseCookie.from("authorization", accessToken)
			.domain(domain)
			.httpOnly(false)
			.secure(true)
			.path("/")
			.maxAge(maxAge)
			.sameSite("Lax")
			.build();
		res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	public void addRefreshTokenCookie(HttpServletResponse res,
		String refreshToken,
		String domain,
		Duration maxAge) {
		ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
			.domain(domain)
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(maxAge)
			.sameSite("None")
			.build();
		res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}
}
