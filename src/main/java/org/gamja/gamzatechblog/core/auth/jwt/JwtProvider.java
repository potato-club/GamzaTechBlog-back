package org.gamja.gamzatechblog.core.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {
    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKeyString;
    private SecretKey secretKey;

    private static final String ACCESS_HEADER = "ACCESS";
    private static final String REFRESH_HEADER = "REFRESH";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CLAIM_GITHUB_ID = "githubId";
    private static final String TOKEN_SUBJECT = "감자테크블로그";

    private static final long ACCESS_TOKEN_VALIDITY_MS  = 30 * 60 * 1000L;
    private static final long REFRESH_TOKEN_VALIDITY_MS = 7L * 24 * 60 * 60 * 1000L;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String githubId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(TOKEN_SUBJECT)
                .claim(CLAIM_GITHUB_ID, githubId)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALIDITY_MS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String githubId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(TOKEN_SUBJECT)
                .claim(CLAIM_GITHUB_ID, githubId)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALIDITY_MS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String resolveAccessToken(HttpServletRequest req) {
        return resolveToken(req.getHeader(ACCESS_HEADER));
    }

    public String resolveRefreshToken(HttpServletRequest req) {
        return resolveToken(req.getHeader(REFRESH_HEADER));
    }

    private String resolveToken(String header) {
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, "Access");
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, "Refresh");
    }

    private boolean validateToken(String token, String type) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.info("{} Token 만료: {}", type, e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("{} Token 검증 실패: {}", type, e.getMessage());
        }
        return false;
    }

    public String getGithubId(String token) {
        Claims body = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getBody();
        return body.get(CLAIM_GITHUB_ID, String.class);
    }

    public String setInvalidAuthenticationMessage(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return "Logic Error : Backend 확인 필요";
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            return ErrorCode.UNSUPPORTED_JWT.getMessage();
        } catch (ExpiredJwtException e) {
            return ErrorCode.EXPIRED_JWT.getMessage();
        } catch (SignatureException e) {
            return ErrorCode.SIGNATURE_INVALID_JWT.getMessage();
        } catch (IllegalArgumentException e) {
            return ErrorCode.JWT_NOT_FOUND.getMessage();
        }
    }

    public Authentication getAuthentication(String token) {
        String githubId = getGithubId(token);
        UserDetails ud = userDetailsService.loadUserByUsername(githubId);
        return new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
    }
}
