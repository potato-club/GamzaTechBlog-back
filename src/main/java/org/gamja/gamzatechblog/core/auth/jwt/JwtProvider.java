package org.gamja.gamzatechblog.core.auth.jwt;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.gamja.gamzatechblog.core.auth.dto.TokenResponse;
import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {
	private final UserRepository userRepository;

	@Value("${jwt.secret}")
	private String secretKeyString;
	private SecretKey secretKey;

	private static final String BEARER_PREFIX = "Bearer ";
	private static final String CLAIM_GITHUB_ID = "githubId";
	private static final String TOKEN_SUBJECT = "GamjaTech";

	private static final long ACCESS_TOKEN_VALIDITY_MS = 30 * 60 * 1000L;
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
		String token = resolveToken(req.getHeader("ACCESS"));
		if (token != null)
			return token;
		return resolveToken(req.getHeader("Authorization"));
	}

	public String resolveRefreshToken(HttpServletRequest req) {
		if (req.getCookies() == null)
			return null;
		for (Cookie c : req.getCookies()) {
			if ("refreshToken".equals(c.getName())) {
				return resolveToken(c.getValue());
			}
		}
		return null;
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

	public Claims parseClaimsOrThrow(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			throw new BusinessException(ErrorCode.EXPIRED_JWT);
		} catch (UnsupportedJwtException | MalformedJwtException e) {
			throw new BusinessException(ErrorCode.UNSUPPORTED_JWT);
		} catch (SignatureException e) {
			throw new BusinessException(ErrorCode.SIGNATURE_INVALID_JWT);
		} catch (IllegalArgumentException e) {
			throw new BusinessException(ErrorCode.JWT_NOT_FOUND);
		}
	}

	public Authentication getAuthentication(String token) {
		String githubId = getGithubId(token);
		User user = userRepository.findByGithubId(githubId)
			.orElseThrow(() -> new UsernameNotFoundException("User not found: " + githubId));
		Collection<GrantedAuthority> auths = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

		return new UsernamePasswordAuthenticationToken(user, null, auths);
	}

	public String extractGithubIdFromRefreshToken(String refreshToken) {
		if (!validateRefreshToken(refreshToken)) {
			throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "리프레시 토큰이 유효하지 않습니다.");
		}
		Claims claims = parseClaimsOrThrow(refreshToken);
		return claims.get(CLAIM_GITHUB_ID, String.class);
	}

	public void addTokenHeaders(HttpServletResponse res, TokenResponse tokens) {
		String bearer = BEARER_PREFIX + tokens.getAccessToken();
		res.setHeader("Authorization", bearer);
	}
}