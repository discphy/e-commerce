package kr.hhplus.be.ecommerce.infrastructure.lock;

import kr.hhplus.be.ecommerce.support.lock.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpinLockTemplate extends DefaultLockTemplate {

    private static final String UNLOCK_SCRIPT = """
        if redis.call("get", KEYS[1]) == ARGV[1] then
            return redis.call("del", KEYS[1])
        else
            return 0
        end
    """;

    private final StringRedisTemplate redisTemplate;
    private final LockIdHolder lockIdHolder;

    @Override
    public LockStrategy getLockStrategy() {
        return LockStrategy.SPIN_LOCK;
    }

    @Override
    public void acquireLock(String key, long waitTime, long leaseTime, TimeUnit timeUnit) {
        long startTime = System.currentTimeMillis();
        String lockId = UUID.randomUUID().toString();

        lockIdHolder.set(key, lockId);

        log.debug("락 획득 시도 : {}", key);
        while (!tryLock(key, lockId, leaseTime, timeUnit)) {
            log.debug("락 획득 대기 중 : {}", key);

            if (timeout(startTime, waitTime, timeUnit)) {
                throw new IllegalStateException("락 획득 대기 시간 초과 : " + key);
            }

            Thread.onSpinWait();
        }
    }

    @Override
    public void releaseLock(String key) {
        if (lockIdHolder.notExists(key)) {
            log.debug("락 해제 실패 : 락을 보유하고 있지 않음 : {}", key);
            return;
        }

        String lockId = lockIdHolder.get(key);
        unlock(key, lockId);

        lockIdHolder.remove(lockId);
        log.debug("락 해제 : {}", key);
    }

    private boolean tryLock(String key, String lockId, long leaseTime, TimeUnit timeUnit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, lockId, leaseTime, timeUnit));
    }

    private boolean timeout(long startTime, long waitTime, TimeUnit timeUnit) {
        return System.currentTimeMillis() - startTime > timeUnit.toMillis(waitTime);
    }

    private void unlock(String key, String lockId) {
        redisTemplate.execute(
            new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class),
            Collections.singletonList(key),
            lockId
        );
    }
}
