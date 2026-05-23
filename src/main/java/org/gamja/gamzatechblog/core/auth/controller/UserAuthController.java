package org.gamja.gamzatechblog.core.auth.controller;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.core.auth.dto.AccessTokenResponse;
import org.gamja.gamzatechblog.core.auth.dto.TokenResponse;
import org.gamja.gamzatechblog.core.config.security.GithubLoginProperties;
import org.gamja.gamzatechblog.core.auth.oauth.util.CookieUtils;
import org.gamja.gamzatechblog.core.auth.service.AuthService;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@ApiController("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {

	private final AuthService authService;
	private final CookieUtils cookieUtils;
	private final GithubLoginProperties githubLoginProperties;

	//소셜로그인 API를 따로 받지않고 url입력시 내부에서 자동으로 처리해 토큰 발급합니다.

	@Operation(summary = "토큰 재발급", tags = {"인증,인가"})
	@PostMapping("/reissue")
	public ResponseDto<AccessTokenResponse> reissue(
		@CookieValue(value = "refreshToken", required = false) String oldRefresh, HttpServletResponse response) {
		if (oldRefresh == null) {
			return ResponseDto.of(
				HttpStatus.UNAUTHORIZED,
				"리프레시 토큰이 없습니다.",
				null
			);
		}
		TokenResponse tokens = authService.reissueRefreshToken(oldRefresh);
		cookieUtils.addAccessTokenCookie(
			response,
			tokens.getAccessToken(),
			githubLoginProperties.getCookieDomain(),
			githubLoginProperties.getAccessTokenTtl()
		);
		AccessTokenResponse body = new AccessTokenResponse(tokens.getAccessToken());
		return ResponseDto.of(HttpStatus.OK, "토큰 재발급 성공", body);
	}

	@Operation(summary = "로그아웃", tags = {"인증,인가"})
	@PostMapping("/me/logout")
	public ResponseDto<String> logout(@CurrentUser User user, HttpServletResponse response) {
		authService.logout(user.getGithubId());
		cookieUtils.expireAccessTokenCookie(response, githubLoginProperties.getCookieDomain());
		cookieUtils.expireRefreshTokenCookie(response, githubLoginProperties.getCookieDomain());

		return ResponseDto.of(HttpStatus.OK, "로그아웃되었습니다.");
	}

}
