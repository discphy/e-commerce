package kr.hhplus.be.ecommerce.cache.domain;

import java.time.Duration;

public interface Cacheable {

    String createKey(String key);

    String cacheName();

    Duration ttl();
}
