package org.gamja.gamzatechblog.core.auth.oauth.dao;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenDao {
	private static final String TOKEN_PREFIX = "refresh:";
	private static final String USER_TOKEN_KEY = "userRefresh:";
	private final StringRedisTemplate redis;

	public RefreshTokenDao(@Qualifier("authStringRedisTemplate") StringRedisTemplate redis) {
		this.redis = redis;
	}

	public void storeRefreshToken(String refreshToken, String userId, Duration ttl) {
		redis.opsForValue().set(TOKEN_PREFIX + refreshToken, userId, ttl);
	}

	public Optional<String> findUserIdByRefreshToken(String refreshToken) {
		return Optional.ofNullable(redis.opsForValue().get(TOKEN_PREFIX + refreshToken))
			.map(Object::toString);
	}

	public void removeRefreshToken(String refreshToken) {
		redis.delete(TOKEN_PREFIX + refreshToken);
	}

	public void rotateRefreshToken(String userId, String newToken, Duration ttl) {
		String oldToken = (String)redis.opsForValue().get(USER_TOKEN_KEY + userId);
		if (oldToken != null) {
			redis.delete(TOKEN_PREFIX + oldToken);
		}

		redis.opsForValue().set(TOKEN_PREFIX + newToken, userId, ttl);
		redis.opsForValue().set(USER_TOKEN_KEY + userId, newToken, ttl);
	}
}
