package kr.hhplus.be.ecommerce.support.database;

import kr.hhplus.be.ecommerce.support.cache.CacheType;
import kr.hhplus.be.ecommerce.support.cache.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Set;

@Component
@Profile("test")
public class RedisCacheCleaner {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void clean() {
        Arrays.stream(CacheType.values())
            .map(Cacheable::cacheName)
            .map(this::getKeys)
            .filter(this::isExistKeys)
            .forEach(this::cleanBy);
    }

    private Set<String> getKeys(String name) {
        String keyPattern = getKeyPattern(name);
        return stringRedisTemplate.keys(keyPattern);
    }

    private String getKeyPattern(String name) {
        return name + "*";
    }

    private boolean isExistKeys(Set<String> keys) {
        return !CollectionUtils.isEmpty(keys);
    }

    private Long cleanBy(Set<String> keys) {
        return stringRedisTemplate.delete(keys);
    }
}
