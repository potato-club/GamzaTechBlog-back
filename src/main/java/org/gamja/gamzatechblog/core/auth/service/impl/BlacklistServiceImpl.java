package org.gamja.gamzatechblog.core.auth.service.impl;

import java.util.concurrent.TimeUnit;

import org.gamja.gamzatechblog.core.auth.jwt.JwtProvider;
import org.gamja.gamzatechblog.core.auth.service.BlacklistService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class BlacklistServiceImpl implements BlacklistService {
	private final RedisTemplate<String, String> redisTemplate;
	private final JwtProvider jwtProvider;

	public BlacklistServiceImpl(
		@Qualifier("authStringRedisTemplate") RedisTemplate<String, String> redisTemplate, JwtProvider jwtProvider) {
		this.redisTemplate = redisTemplate;
		this.jwtProvider = jwtProvider;
	}

	/*
	로그아웃 로직입니다. 로그아웃시, 액세스토큰을 블랙리스트 합니다.
	 */
	public void blacklistTokens(String githubId) {
		String refreshKey = "refresh:" + githubId;
		redisTemplate.delete(refreshKey);
		String accessToken = jwtProvider.resolveAccessTokenFromHeader();
		if (accessToken != null) {
			long remainingSec = jwtProvider.getRemainingAccessTokenValidity(accessToken);
			String blackKey = "blacklist:access:" + accessToken;
			redisTemplate.opsForValue().set(blackKey, "true", remainingSec, TimeUnit.SECONDS);
		}
	}
}
