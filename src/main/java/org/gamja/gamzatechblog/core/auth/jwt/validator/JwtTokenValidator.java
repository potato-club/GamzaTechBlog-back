package org.gamja.gamzatechblog.core.auth.jwt.validator;

import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.JwtAuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenValidator {
	private final JwtProvider jwtProvider;

	@Autowired
	@Qualifier("authStringRedisTemplate")
	private RedisTemplate<String, String> redisTemplate;

	public JwtTokenValidator(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}

	public String resolveAndValidate(HttpServletRequest request) {
		String token = jwtProvider.resolveAccessToken(request);
		String blackKey = "blacklist:access:" + token;
		if (token == null) {
			throw new JwtAuthenticationException(ErrorCode.JWT_NOT_FOUND);
		}
		if (!jwtProvider.validateAccessToken(token)) {
			throw new JwtAuthenticationException(ErrorCode.INVALID_TYPE_VALUE);
		}
		if (Boolean.TRUE.equals(redisTemplate.hasKey(blackKey))) {
			throw new JwtAuthenticationException(ErrorCode.EXPIRED_JWT);
		}
		return token;
	}
}
