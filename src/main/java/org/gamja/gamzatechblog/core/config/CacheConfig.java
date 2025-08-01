package org.gamja.gamzatechblog.core.config;

import java.time.Duration;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

	private final LettuceConnectionFactory redisConnectionFactory;

	@Bean
	public RedisCacheManager cacheManager() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.activateDefaultTyping(
			LaissezFaireSubTypeValidator.instance,
			ObjectMapper.DefaultTyping.EVERYTHING,
			JsonTypeInfo.As.PROPERTY
		);

		GenericJackson2JsonRedisSerializer jsonSerializer =
			new GenericJackson2JsonRedisSerializer(mapper);

		RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofMinutes(5))
			.disableCachingNullValues()
			.computePrefixWith(cacheName -> "cache:" + cacheName + ":")
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer)
			);

		Map<String, RedisCacheConfiguration> configs = Map.of(
			"hotPosts", defaultConfig.entryTtl(Duration.ofMinutes(30)),
			"frequentPosts", defaultConfig.entryTtl(Duration.ofMinutes(10))
		);

		return RedisCacheManager.builder(redisConnectionFactory)
			.cacheDefaults(defaultConfig)
			.withInitialCacheConfigurations(configs)
			.build();
	}
}
