package org.gamja.gamzatechblog.core.auth.oauth.client;

import org.springframework.http.ResponseCookie;

public final class CookieUtil {

	private CookieUtil() {
	}

	public static ResponseCookie create(
		String name, String value,
		String domain, String path,
		long maxAgeSec, boolean httpOnly, String sameSite) {

		return ResponseCookie.from(name, value)
			.domain(domain)
			.path(path)
			.secure(true)
			.httpOnly(httpOnly)
			.sameSite(sameSite)
			.maxAge(maxAgeSec)
			.build();
	}

	public static ResponseCookie delete(
		String name, String domain, String path,
		boolean httpOnly, String sameSite) {
		return create(name, "", domain, path, 0, httpOnly, sameSite);
	}
}

