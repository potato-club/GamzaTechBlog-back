package org.gamja.gamzatechblog.core.auth.controller;

import org.gamja.gamzatechblog.core.auth.dto.RefreshRequest;
import org.gamja.gamzatechblog.core.auth.dto.TokenResponse;
import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.auth.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {

	private final JwtProvider jwtProvider;
	private final AuthService authService;

	//소셜로그인 API를 따로 받지않고 url입력시 내부에서 자동으로 처리해 토큰 발급합니다.

	@Operation(summary = "토큰 재발급", tags = {"인증,인가"})
	@PostMapping("/reissue")
	public void reissue(@RequestBody RefreshRequest req, HttpServletResponse resp) {
		TokenResponse token = authService.reissueRefreshToken(req.getRefreshToken());
		jwtProvider.addTokenHeaders(resp, token);
	}
}
