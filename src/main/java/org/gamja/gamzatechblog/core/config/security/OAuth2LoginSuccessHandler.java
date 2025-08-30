package org.gamja.gamzatechblog.core.config.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;

import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.auth.oauth.dao.RefreshTokenDao;
import org.gamja.gamzatechblog.core.auth.oauth.event.GithubPostLoginEvent;
import org.gamja.gamzatechblog.core.auth.oauth.model.GithubUser;
import org.gamja.gamzatechblog.core.auth.oauth.util.CookieUtils;
import org.gamja.gamzatechblog.domain.user.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final UserService userService;
	private final JwtProvider jwtProvider;
	private final OAuth2AuthorizedClientService authorizedClientService;
	private final RefreshTokenDao refreshTokenDao;
	private final CookieUtils cookieUtils;
	private final ApplicationEventPublisher publisher;

	private static final String COOKIE_DOMAIN = ".gamzatech.site";
	private static final Duration ACCESS_TOKEN_TTL = Duration.ofHours(1);
	private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(30);
	private static final Set<String> ALLOWED_LOCATIONS = Set.of("app", "dev", "preview");
	private static final String DEFAULT_LOCATION = "app";

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		long t0 = System.nanoTime();

		OAuth2User oauth2User = (OAuth2User)authentication.getPrincipal();
		GithubUser gitUser = new GithubUser(oauth2User.getAttributes());
		String githubId = gitUser.getGithubId();

		OAuth2AuthorizedClient client = resolveAuthorizedClient(authentication);

		registerIfNewUser(githubId, gitUser); // 이메일 없어도 등록 가능하도록

		String accessToken = jwtProvider.createAccessToken(githubId);
		String refreshToken = jwtProvider.createRefreshToken(githubId);
		refreshTokenDao.rotateRefreshToken(githubId, refreshToken, REFRESH_TOKEN_TTL);
		cookieUtils.addAccessTokenCookie(response, accessToken, COOKIE_DOMAIN, ACCESS_TOKEN_TTL);
		cookieUtils.addRefreshTokenCookie(response, refreshToken, COOKIE_DOMAIN, REFRESH_TOKEN_TTL);

		if (client != null && client.getAccessToken() != null) {
			publisher.publishEvent(new GithubPostLoginEvent(
				githubId,
				client.getAccessToken().getTokenValue()
			));
		}

		String redirectUrl = resolveRedirectUrl(request);
		log.debug("oauth2_success_flow_ms={}", (System.nanoTime() - t0) / 1_000_000);
		response.sendRedirect(redirectUrl);
	}

	private OAuth2AuthorizedClient resolveAuthorizedClient(Authentication authentication) {
		String registrationId = (authentication instanceof OAuth2AuthenticationToken t)
			? t.getAuthorizedClientRegistrationId()
			: "github";
		return authorizedClientService.loadAuthorizedClient(registrationId, authentication.getName());
	}

	private void registerIfNewUser(String githubId, GithubUser gitUser) {
		if (!userService.existsByGithubId(githubId)) {
			userService.registerWithProvider(gitUser);
		}
	}

	private String resolveRedirectUrl(HttpServletRequest request) {
		String loc = request.getParameter("location");
		if (loc != null)
			loc = loc.trim().toLowerCase();
		if (loc == null || !ALLOWED_LOCATIONS.contains(loc)) {
			loc = DEFAULT_LOCATION;
			log.debug("location 파라미터가 없거나 허용되지 않아 기본값으로 리다이렉트: {}", loc);
		}
		return String.format("https://%s.gamzatech.site", loc);
	}
}
