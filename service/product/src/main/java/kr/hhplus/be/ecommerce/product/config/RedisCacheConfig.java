package kr.hhplus.be.ecommerce.product.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import kr.hhplus.be.ecommerce.cache.domain.CacheType;
import kr.hhplus.be.ecommerce.cache.domain.Cacheable;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    private static final Duration DEFAULT_TTL = Duration.ofHours(1);

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = getConfigWith(DEFAULT_TTL);

        Map<String, RedisCacheConfiguration> configs = Arrays.stream(CacheType.values())
            .map(this::createConfig)
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(configs)
            .build();
    }

    private Map.Entry<String, RedisCacheConfiguration> createConfig(Cacheable cacheable) {
        RedisCacheConfiguration config = getConfigWith(cacheable.ttl());
        return Map.entry(cacheable.cacheName(), config);
    }

    private RedisCacheConfiguration getConfigWith(Duration ttl) {
        return RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())))
            .entryTtl(ttl);
    }

    private ObjectMapper objectMapper() {
        return new ObjectMapper()
            .activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
            );
    }
}
