package org.gamja.gamzatechblog.core.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import org.gamja.gamzatechblog.core.auth.dto.TokenResponse;
import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.auth.oauth.client.GithubApiClient;
import org.gamja.gamzatechblog.core.auth.oauth.dao.GithubOAuthTokenDao;
import org.gamja.gamzatechblog.core.auth.oauth.dao.RefreshTokenDao;
import org.gamja.gamzatechblog.core.auth.oauth.model.GithubUser;
import org.gamja.gamzatechblog.domain.user.service.UserService;
import org.springframework.http.ResponseCookie;
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

	private final UserService userService;
	private final JwtProvider jwtProvider;
	private final OAuth2AuthorizedClientService authorizedClientService;
	private final GithubApiClient githubApiClient;
	private final RefreshTokenDao refreshTokenDao;
	private final GithubOAuthTokenDao githubTokenDao;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		OAuth2User oauth2User = (OAuth2User)authentication.getPrincipal();
		Map<String, Object> attr = oauth2User.getAttributes();
		GithubUser gitUser = new GithubUser(attr);
		String githubId = gitUser.getGithubId();

		OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient("github",
			authentication.getName());

		if (!userService.existsByGithubId(gitUser.getGithubId())) {
			userService.registerWithProvider(gitUser);
		}

		if (client != null && client.getAccessToken() != null) {
			String githubAccessToken = client.getAccessToken().getTokenValue();
			githubTokenDao.saveOrUpdateByGithubId(githubId, githubAccessToken);
			try {
				String email = githubApiClient.fetchPrimaryEmail(githubAccessToken);
				gitUser.setEmail(email);
			} catch (Exception e) {
				log.warn("GitHub 이메일 조회 실패: {}", e.getMessage());
			}
		}
		String accessToken = jwtProvider.createAccessToken(githubId);
		String refreshToken = jwtProvider.createRefreshToken(githubId);

		refreshTokenDao.rotateRefreshToken(githubId, refreshToken, Duration.ofDays(30));
		jwtProvider.addTokenHeaders(response, new TokenResponse(accessToken, refreshToken));

		ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
			.domain(".gamzatech.site")
			.httpOnly(true)
			.secure(true) //Secure, sameSite 상황에따라 변경
			.path("/")
			.maxAge(Duration.ofDays(30))
			.sameSite("None")
			.build();
		response.addHeader("Set-Cookie", cookie.toString());

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write("{}");
	}
}