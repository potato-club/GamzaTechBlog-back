package org.gamja.gamzatechblog.core.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

public abstract class RedisConfig {

	public RedisConnectionFactory createLettuceConnectionFactory(String host, int port, String password, int dbIndex) {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setHostName(host);
		config.setPort(port);
		if (password != null && !password.isEmpty()) {
			config.setPassword(RedisPassword.of(password));
		}
		config.setDatabase(dbIndex);
		return new LettuceConnectionFactory(config);
	}
}
