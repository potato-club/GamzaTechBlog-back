package org.gamja.gamzatechblog.core.auth.jwt;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.gamja.gamzatechblog.core.config.security.GithubLoginProperties;
import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.BusinessException;
import org.gamja.gamzatechblog.core.error.exception.UnauthorizedException;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.service.port.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {
	private final UserRepository userRepository;
	private final GithubLoginProperties githubLoginProperties;

	@Value("${jwt.secret}")
	private String secretKeyString;
	private SecretKey secretKey;

	private static final String BEARER_PREFIX = "Bearer ";
	private static final String CLAIM_GITHUB_ID = "githubId";
	private static final String TOKEN_SUBJECT = "GamjaTech";

	@PostConstruct
	public void init() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
		this.secretKey = Keys.hmacShaKeyFor(keyBytes);
	}

	public String createAccessToken(String githubId) {
		return createToken(githubId, githubLoginProperties.getAccessTokenTtl().toMillis());
	}

	public String createRefreshToken(String githubId) {
		return createToken(githubId, githubLoginProperties.getRefreshTokenTtl().toMillis());
	}

	private String createToken(String githubId, long validityMs) {
		Date now = new Date();
		return Jwts.builder()
			.setSubject(TOKEN_SUBJECT)
			.claim(CLAIM_GITHUB_ID, githubId)
			.setId(UUID.randomUUID().toString())
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + validityMs))
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
			return !parseClaims(token).getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			log.info("{} Token 만료: {}", type, e.getMessage());
		} catch (JwtException | IllegalArgumentException e) {
			log.warn("{} Token 검증 실패: {}", type, e.getMessage());
		}
		return false;
	}

	public String getGithubId(String token) {
		return parseClaims(token).get(CLAIM_GITHUB_ID, String.class);
	}

	public Claims parseClaimsOrThrow(String token) {
		try {
			return parseClaims(token);
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

	private Claims parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
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
			throw new UnauthorizedException(ErrorCode.AUTHENTICATION_FAILED);
		}
		return parseClaimsOrThrow(refreshToken).get(CLAIM_GITHUB_ID, String.class);
	}

	public String resolveAccessTokenFromHeader() {
		ServletRequestAttributes attrs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (attrs == null)
			return null;
		HttpServletRequest req = attrs.getRequest();
		String raw = Optional.ofNullable(req.getHeader("ACCESS")).orElse(req.getHeader("Authorization"));
		return resolveToken(raw);
	}

	public long getRemainingAccessTokenValidity(String token) {
		Date exp = parseClaims(token).getExpiration();
		long secondsLeft = (exp.getTime() - System.currentTimeMillis()) / 1000;
		return Math.max(secondsLeft, 0L);
	}
}
