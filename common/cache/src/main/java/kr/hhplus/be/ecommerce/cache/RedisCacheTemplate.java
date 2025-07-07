package kr.hhplus.be.ecommerce.cache;

import kr.hhplus.be.ecommerce.cache.domain.CacheTemplate;
import kr.hhplus.be.ecommerce.cache.domain.Cacheable;
import kr.hhplus.be.ecommerce.serialize.DataSerializer;
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
            .map(value -> DataSerializer.deserialize(value, type));
    }

    @Override
    public <T> void put(Cacheable cacheable, String key, T value) {
        String createdKey = cacheable.createKey(key);
        String serializeValue = Optional.ofNullable(DataSerializer.serialize(value))
            .orElseThrow(() -> new IllegalArgumentException("문자열 변환에 실패하였습니다."));

        redisTemplate.opsForValue().set(createdKey, serializeValue, cacheable.ttl());
    }

    @Override
    public void evict(Cacheable cacheable, String key) {
        String createdKey = cacheable.createKey(key);
        Boolean deleted = redisTemplate.delete(createdKey);

        if (FALSE.equals(deleted)) {
            log.debug("삭제할 캐시가 존재하지 않습니다. key: {}", createdKey);
        }
    }
}
