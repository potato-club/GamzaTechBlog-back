package org.gamja.gamzatechblog.core.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.auth.dto.TokenResponse;
import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.auth.oauth.client.GithubApiClient;
import org.gamja.gamzatechblog.core.auth.oauth.dao.GithubOAuthTokenDao;
import org.gamja.gamzatechblog.core.auth.oauth.dao.RefreshTokenDao;
import org.gamja.gamzatechblog.core.auth.oauth.model.GithubUser;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	private final ObjectMapper objectMapper;

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

		User user = userService.findByGithubId(githubId);

		String accessToken = jwtProvider.createAccessToken(githubId);
		String refreshToken = jwtProvider.createRefreshToken(githubId);

		refreshTokenDao.rotateRefreshToken(githubId, refreshToken, Duration.ofDays(30));
		jwtProvider.addTokenHeaders(response, new TokenResponse(accessToken, null));

		ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
			.httpOnly(true)
			.secure(false)               // 배포 시 HTTPS면 true 로 변경
			.path("/")
			.maxAge(Duration.ofDays(30))
			.sameSite("Lax")
			.build();
		response.addHeader("Set-Cookie", cookie.toString());

		boolean complete = user.isProfileComplete();
		ResponseDto<Map<String, Boolean>> body = ResponseDto.of(HttpStatus.OK, "로그인 성공",
			Map.of("profileComplete", complete));
		response.setContentType("application/json;charset=UTF-8");
		objectMapper.writeValue(response.getWriter(), body);
	}
}
