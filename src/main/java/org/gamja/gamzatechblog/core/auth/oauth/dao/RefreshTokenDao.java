package org.gamja.gamzatechblog.core.auth.oauth.dao;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class RefreshTokenDao {
	private static final String TOKEN_PREFIX = "refresh:";
	private static final String USER_TOKEN_KEY = "userRefresh:";
	private static final String ROTATE_REFRESH_TOKEN_SCRIPT = """
		local userKey = KEYS[1]
		local newTokenKey = KEYS[2]
		local tokenPrefix = ARGV[1]
		local userId = ARGV[2]
		local newToken = ARGV[3]
		local ttl = tonumber(ARGV[4])

		local oldToken = redis.call('GET', userKey)
		if oldToken then
			redis.call('DEL', tokenPrefix .. oldToken)
		end

		redis.call('SET', newTokenKey, userId, 'EX', ttl)
		redis.call('SET', userKey, newToken, 'EX', ttl)
		return 1
		""";
	private static final RedisScript<Long> ROTATE_SCRIPT =
		new DefaultRedisScript<>(ROTATE_REFRESH_TOKEN_SCRIPT, Long.class);
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
		long ttlSeconds = Math.max(ttl.getSeconds(), 1L);
		try {
			redis.execute(
				ROTATE_SCRIPT,
				List.of(USER_TOKEN_KEY + userId, TOKEN_PREFIX + newToken),
				TOKEN_PREFIX,
				userId,
				newToken,
				Long.toString(ttlSeconds)
			);
			return;
		} catch (DataAccessException ex) {
			log.warn("Lua refresh rotate 실패, 레거시 회전으로 폴백 userId={} cause={}", userId, ex.getMessage());
		}

		rotateRefreshTokenLegacy(userId, newToken, ttl);
	}

	private void rotateRefreshTokenLegacy(String userId, String newToken, Duration ttl) {
		String oldToken = (String)redis.opsForValue().get(USER_TOKEN_KEY + userId);
		if (oldToken != null) {
			redis.delete(TOKEN_PREFIX + oldToken);
		}

		redis.opsForValue().set(TOKEN_PREFIX + newToken, userId, ttl);
		redis.opsForValue().set(USER_TOKEN_KEY + userId, newToken, ttl);
	}

	public void touchTtl(String refreshToken, Duration ttl) {
		String key = TOKEN_PREFIX + refreshToken;
		redis.expire(key, ttl);
	}
}
