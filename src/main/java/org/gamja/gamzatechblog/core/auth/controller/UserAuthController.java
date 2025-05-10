package org.gamja.gamzatechblog.core.auth.controller;

import org.gamja.gamzatechblog.core.auth.dto.RefreshRequest;
import org.gamja.gamzatechblog.core.auth.dto.TokenResponse;
import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.core.auth.oauth.service.OAuthService;
import org.gamja.gamzatechblog.domain.user.service.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final OAuthService oAuthService;
    private final UserAuthService userAuthService;
    private final JwtProvider jwtProvider;

    @GetMapping("/login/github/callback")
    public ResponseEntity<TokenResponse> loginWithGithub(@RequestParam String code) {
        OAuthUserInfo info = oAuthService.getUserInfoFromGithub(code);

        if (!userAuthService.existsByProviderId(info.getProviderId())) {
            userAuthService.registerWithProvider(info);
        }

        String access  = jwtProvider.createAccessToken(info.getProviderId());
        String refresh = jwtProvider.createRefreshToken(info.getProviderId());

        return ResponseEntity.ok(new TokenResponse(access, refresh));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@RequestBody RefreshRequest req) {
        String refreshToken = req.getRefreshToken();

        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(403).build();
        }

        String providerId  = jwtProvider.getGithubId(refreshToken);
        String newAccess   = jwtProvider.createAccessToken(providerId);
        String newRefresh  = jwtProvider.createRefreshToken(providerId);

        return ResponseEntity.ok(new TokenResponse(newAccess, newRefresh));
    }
}
