package kr.hhplus.be.ecommerce.infrastructure.lock;

import kr.hhplus.be.ecommerce.support.MockTestSupport;
import kr.hhplus.be.ecommerce.support.lock.LockCallback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PubSubLockTemplateUnitTest extends MockTestSupport {

    @InjectMocks
    private PubSubLockTemplate lockTemplate;

    @Mock
    private RedissonClient redissonClient;

    @DisplayName("락을 획득하지 못하면 예외가 발생한다.")
    @Test
    void executeWithLockWhenNotAcquired() throws InterruptedException {
        // given
        RLock lock = mock(RLock.class);
        LockCallback<String> callback = () -> "callback";

        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(false);
        when(lock.isHeldByCurrentThread()).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("락 획득 실패");
    }

    @DisplayName("락을 획득하면 콜백을 실행한다.")
    @Test
    void executeWithLockWhenAcquired() throws Throwable {
        // given
        RLock lock = mock(RLock.class);
        LockCallback<String> callback = () -> "callback";

        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);

        // when
        String result = lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback);

        // then
        assertThat(result).isEqualTo("callback");
    }

    @DisplayName("락을 해제한다.")
    @Test
    void executeWithLockAfterUnlock() throws Throwable {
        // given
        RLock lock = mock(RLock.class);
        LockCallback<String> callback = () -> "callback";

        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(true);

        // when
        lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback);

        // then
        verify(lock, times(1)).unlock();
    }

    @DisplayName("락 해제시, 현재 스레드가 락을 보유하고 있지 않으면 아무것도 하지 않는다.")
    @Test
    void executeWithLockWhenNotHeldByCurrentThread() throws Throwable {
        // given
        RLock lock = mock(RLock.class);
        LockCallback<String> callback = () -> "callback";

        when(redissonClient.getLock(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(lock.isHeldByCurrentThread()).thenReturn(false);

        // when
        lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback);

        // then
        verify(lock, never()).unlock();
    }
}