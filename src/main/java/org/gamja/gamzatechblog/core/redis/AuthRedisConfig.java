package org.gamja.gamzatechblog.core.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class AuthRedisConfig extends RedisConfig {

	@Value("${spring.data.redis.auth.host}")
	private String host;
	@Value("${spring.data.redis.auth.port}")
	private int port;
	@Value("${spring.data.redis.auth.password}")
	private String password;
	@Value("${spring.data.redis.auth.database}")
	private int database;

	@Bean
	@Primary
	public RedisConnectionFactory authRedisConnectionFactory() {
		return createLettuceConnectionFactory(host, port, password, database);
	}

	@Bean
	@Primary
	public RedisTemplate<String, Object> authRedisTemplate(RedisConnectionFactory authRedisConnectionFactory) {
		RedisTemplate<String, Object> tpl = new RedisTemplate<>();
		tpl.setConnectionFactory(authRedisConnectionFactory);
		tpl.setKeySerializer(new StringRedisSerializer());
		tpl.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		tpl.setHashKeySerializer(new StringRedisSerializer());
		tpl.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		return tpl;
	}

	@Bean("authStringRedisTemplate")
	public StringRedisTemplate authStringRedisTemplate(RedisConnectionFactory authRedisConnectionFactory) {
		StringRedisTemplate tpl = new StringRedisTemplate();
		tpl.setConnectionFactory(authRedisConnectionFactory);
		tpl.setKeySerializer(new StringRedisSerializer());
		tpl.setValueSerializer(new StringRedisSerializer());
		return tpl;
	}
}
