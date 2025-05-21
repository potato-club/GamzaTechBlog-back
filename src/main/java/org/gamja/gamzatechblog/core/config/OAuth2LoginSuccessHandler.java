package org.gamja.gamzatechblog.core.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import org.gamja.gamzatechblog.core.auth.dto.TokenResponse;
import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.auth.oauth.client.GithubApiClient;
import org.gamja.gamzatechblog.core.auth.oauth.dao.RefreshTokenDao;
import org.gamja.gamzatechblog.core.auth.oauth.model.GithubUser;
import org.gamja.gamzatechblog.domain.user.service.UserAuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final UserAuthService userAuthService;
	private final JwtProvider jwtProvider;
	private final OAuth2AuthorizedClientService authorizedClientService;
	private final GithubApiClient githubApiClient;
	private final RefreshTokenDao refreshTokenDao;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		OAuth2User oauth2User = (OAuth2User)authentication.getPrincipal();
		Map<String, Object> attr = oauth2User.getAttributes();
		GithubUser gitUser = new GithubUser(attr);

		OAuth2AuthorizedClient client =
			authorizedClientService.loadAuthorizedClient(
				"github",
				authentication.getName());

		String email = null;
		if (client != null && client.getAccessToken() != null) {
			String tokenValue = client.getAccessToken().getTokenValue();
			try {
				email = githubApiClient.fetchPrimaryEmail(tokenValue);
			} catch (Exception e) {
				log.warn("GitHub 이메일 조회 실패: {}", e.getMessage());
			}
		}
		if (!userAuthService.existsByProviderId(gitUser.getProviderId())) {
			userAuthService.registerWithProvider(gitUser);
		}

		String userId = gitUser.getProviderId();
		String accessToken = jwtProvider.createAccessToken(userId);
		String refreshToken = jwtProvider.createRefreshToken(userId);

		refreshTokenDao.rotateRefreshToken(userId, refreshToken, Duration.ofDays(30));
		jwtProvider.addTokenHeaders(response, new TokenResponse(accessToken, refreshToken));

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write("{}");
	}
}
