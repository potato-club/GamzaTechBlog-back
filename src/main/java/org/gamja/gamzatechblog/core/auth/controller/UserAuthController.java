package org.gamja.gamzatechblog.core.auth.controller;

import java.time.Duration;

import org.gamja.gamzatechblog.common.annotation.CurrentUser;
import org.gamja.gamzatechblog.common.dto.ResponseDto;
import org.gamja.gamzatechblog.core.annotation.ApiController;
import org.gamja.gamzatechblog.core.auth.dto.AccessTokenResponse;
import org.gamja.gamzatechblog.core.auth.dto.TokenResponse;
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
	private static final String DOMAIN = ".gamzatech.site";
	private static final Duration ACCESS_TOKEN_TTL = Duration.ofHours(1);
	private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(30);

	//소셜로그인 API를 따로 받지않고 url입력시 내부에서 자동으로 처리해 토큰 발급합니다.

	@Operation(summary = "토큰 재발급", tags = {"인증,인가"})
	@PostMapping("/reissue")
	public ResponseDto<AccessTokenResponse> reissue(
		@CookieValue(value = "refreshToken", required = false) String oldRefresh, HttpServletResponse response) {
		// 1) 쿠키가 없으면 401 Unauthorized
		if (oldRefresh == null) {
			return ResponseDto.of(
				HttpStatus.UNAUTHORIZED,
				"리프레시 토큰이 없습니다.", //수정 예정입니다.
				null
			);
		}
		TokenResponse tokens = authService.reissueRefreshToken(oldRefresh);
		cookieUtils.addAccessTokenCookie(response, tokens.getAccessToken(), DOMAIN, ACCESS_TOKEN_TTL);
		cookieUtils.addRefreshTokenCookie(response, tokens.getRefreshToken(), DOMAIN, REFRESH_TOKEN_TTL);
		AccessTokenResponse body = new AccessTokenResponse(tokens.getAccessToken());
		return ResponseDto.of(HttpStatus.OK, "토큰 재발급 성공", body);
	}

	@Operation(summary = "로그아웃", tags = {"인증,인가"})
	@PostMapping("/me/logout")
	public ResponseDto<String> logout(@CurrentUser User user) {
		authService.logout(user.getGithubId());
		return ResponseDto.of(HttpStatus.OK, "로그아웃되었습니다.");
	}

}
