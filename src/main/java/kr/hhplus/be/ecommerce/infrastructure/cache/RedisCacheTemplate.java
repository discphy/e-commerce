package kr.hhplus.be.ecommerce.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import kr.hhplus.be.ecommerce.support.cache.CacheTemplate;
import kr.hhplus.be.ecommerce.support.cache.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.lang.Boolean.FALSE;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheTemplate implements CacheTemplate {

    private final StringRedisTemplate redisTemplate;

    @Override
    public <T> Optional<T> get(Cacheable cacheable, String key, Class<T> type) {
        String createdKey = cacheable.createKey(key);

        return Optional.ofNullable(redisTemplate.opsForValue().get(createdKey))
            .map(value -> deserialize(value, type));
    }

    @Override
    public <T> void put(Cacheable cacheable, String key, T value) {
        String createdKey = cacheable.createKey(key);
        redisTemplate.opsForValue().set(createdKey, serialize(value), cacheable.ttl());
    }

    @Override
    public void evict(Cacheable cacheable, String key) {
        String createdKey = cacheable.createKey(key);
        Boolean deleted = redisTemplate.delete(createdKey);

        if (FALSE.equals(deleted)) {
            log.debug("삭제할 캐시가 존재하지 않습니다. key: {}", createdKey);
        }
    }

    private <T> T deserialize(String value, Class<T> type) {
        try {
            return objectMapper().readValue(value, type);
        } catch (Exception e) {
            throw new IllegalArgumentException("캐싱 값을 역직렬화하는데 실패했습니다. " + type.getSimpleName(), e);
        }
    }

    private <T> String serialize(T value) {
        try {
            return objectMapper().writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("캐싱 값을 직렬화하는데 실패했습니다.", e);
        }
    }

    private ObjectMapper objectMapper() {
        return new ObjectMapper()
            .activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
            );
    }
}
