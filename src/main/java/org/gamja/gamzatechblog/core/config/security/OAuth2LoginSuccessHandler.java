package org.gamja.gamzatechblog.core.config.security;

import java.io.IOException;
import java.util.Locale;

import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.auth.oauth.dao.RefreshTokenDao;
import org.gamja.gamzatechblog.core.auth.oauth.event.GithubPostLoginEvent;
import org.gamja.gamzatechblog.core.auth.oauth.model.GithubUser;
import org.gamja.gamzatechblog.core.auth.oauth.util.CookieUtils;
import org.gamja.gamzatechblog.domain.user.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
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
	private final RefreshTokenDao refreshTokenDao;
	private final CookieUtils cookieUtils;
	private final ApplicationEventPublisher publisher;
 	private final GithubLoginProperties githubLoginProperties;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		long t0 = System.nanoTime();

		OAuth2User oauth2User = (OAuth2User)authentication.getPrincipal();
		GithubUser gitUser = new GithubUser(oauth2User.getAttributes());
		String githubId = gitUser.getGithubId();
		String registrationId = resolveRegistrationId(authentication);
		String principalName = authentication.getName();

		registerIfNewUser(githubId, gitUser); // 이메일 없어도 등록 가능하도록

		String accessToken = jwtProvider.createAccessToken(githubId);
		String refreshToken = jwtProvider.createRefreshToken(githubId);
		refreshTokenDao.rotateRefreshToken(githubId, refreshToken, githubLoginProperties.getRefreshTokenTtl());
		cookieUtils.addAccessTokenCookie(
			response,
			accessToken,
			githubLoginProperties.getCookieDomain(),
			githubLoginProperties.getAccessTokenTtl()
		);
		cookieUtils.addRefreshTokenCookie(
			response,
			refreshToken,
			githubLoginProperties.getCookieDomain(),
			githubLoginProperties.getRefreshTokenTtl()
		);

		publisher.publishEvent(new GithubPostLoginEvent(
			githubId,
			principalName,
			registrationId,
			null
		));

		String redirectUrl = resolveRedirectUrl(request);
		log.debug("oauth2_success_flow_ms={}", (System.nanoTime() - t0) / 1_000_000);
		response.sendRedirect(redirectUrl);
	}

	private String resolveRegistrationId(Authentication authentication) {
		return (authentication instanceof OAuth2AuthenticationToken t)
			? t.getAuthorizedClientRegistrationId()
			: "github";
	}

	private void registerIfNewUser(String githubId, GithubUser gitUser) {
		if (!userService.existsByGithubId(githubId)) {
			userService.registerWithProvider(gitUser);
		}
	}

	private String resolveRedirectUrl(HttpServletRequest request) {
		String loc = request.getParameter("location");
		if (loc != null) {
			loc = loc.trim().toLowerCase(Locale.ROOT);
		}
		if (loc == null || !githubLoginProperties.getAllowedLocations().contains(loc)) {
			loc = githubLoginProperties.getDefaultLocation();
			log.debug("location 파라미터가 없거나 허용되지 않아 기본값으로 리다이렉트: {}", loc);
		}
		return String.format("https://%s.%s", loc, githubLoginProperties.getRedirectDomainSuffix());
	}
}
