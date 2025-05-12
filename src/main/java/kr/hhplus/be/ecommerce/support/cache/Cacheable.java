package kr.hhplus.be.ecommerce.support.cache;

import java.time.Duration;

public interface Cacheable {

    String createKey(String key);

    String cacheName();

    Duration ttl();
}
