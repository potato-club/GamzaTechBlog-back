package org.gamja.gamzatechblog.core.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.gamja.gamzatechblog.core.auth.dto.CodeRequest;
import org.gamja.gamzatechblog.core.auth.dto.RefreshRequest;
import org.gamja.gamzatechblog.core.auth.dto.TokenResponse;
import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @Operation(summary = "깃허브 로그인",tags = {"인증,인가"})
    @PostMapping("/login/github")
    public void loginWithGithub(
            @RequestBody CodeRequest req,
            HttpServletResponse response
    ) {
        TokenResponse tokens = authService.loginWithGithub(req.getCode());
        jwtProvider.addTokenHeaders(response, tokens);
    }

    @Operation(summary = "토큰 재발급",tags = {"인증,인가"})
    @PostMapping("/reissue")
    public void reissue(@RequestBody RefreshRequest req, HttpServletResponse response) {
        TokenResponse tokens = authService.reissueRefreshToken(req.getRefreshToken());
        jwtProvider.addTokenHeaders(response, tokens);
    }

    @Operation(summary = "깃허브 로그인",tags = {"인증,인가"})
    @GetMapping("/login/github/callback")
    public void githubCallback(
            @RequestParam("code") String code,
            HttpServletResponse response
    ) {
        TokenResponse tokens = authService.loginWithGithub(code);
        jwtProvider.addTokenHeaders(response, tokens);
    }
}
