package org.gamja.gamzatechblog.core.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheRedisConfig extends RedisConfig {

	@Value("${spring.data.redis.cache.host}")
	private String host;
	@Value("${spring.data.redis.cache.port}")
	private int port;
	@Value("${spring.data.redis.cache.password}")
	private String password;
	@Value("${spring.data.redis.cache.database}")
	private int database;

	@Bean
	public RedisConnectionFactory cacheRedisConnectionFactory() {
		return createLettuceConnectionFactory(host, port, password, database);
	}

	@Bean
	public RedisTemplate<String, Object> cacheRedisTemplate(RedisConnectionFactory cacheRedisConnectionFactory) {
		RedisTemplate<String, Object> tpl = new RedisTemplate<>();
		tpl.setConnectionFactory(cacheRedisConnectionFactory);
		tpl.setKeySerializer(new StringRedisSerializer());
		tpl.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		tpl.setHashKeySerializer(new StringRedisSerializer());
		tpl.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		return tpl;
	}

	@Bean("cacheStringRedisTemplate")
	public StringRedisTemplate cacheStringRedisTemplate(RedisConnectionFactory cacheRedisConnectionFactory) {
		StringRedisTemplate tpl = new StringRedisTemplate();
		tpl.setConnectionFactory(cacheRedisConnectionFactory);
		tpl.setKeySerializer(new StringRedisSerializer());
		tpl.setValueSerializer(new StringRedisSerializer());
		return tpl;
	}
}
