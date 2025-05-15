package kr.hhplus.be.ecommerce.support.cache;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
public enum CacheType implements Cacheable {

    POPULAR_PRODUCT("인기 상품 캐싱") {
        @Override
        public String cacheName() {
            return CacheName.POPULAR_PRODUCT;
        }

        @Override
        public Duration ttl() {
            return Duration.ofHours(49);
        }
    },
    ;

    private final String description;

    @Override
    public String createKey(String key) {
        return cacheName() + ":" + key;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class CacheName {

        public static final String POPULAR_PRODUCT = "popular-product";
    }
}
