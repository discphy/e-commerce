package kr.hhplus.be.ecommerce.infrastructure.lock;

import kr.hhplus.be.ecommerce.support.MockTestSupport;
import kr.hhplus.be.ecommerce.support.lock.LockCallback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class SpinLockTemplateUnitTest extends MockTestSupport {

    @InjectMocks
    private SpinLockTemplate lockTemplate;

    @Mock
    private StringRedisTemplate redisTemplate;

    @DisplayName("락을 획득하지 못하면 재시도 후 대기 시간을 초과하여 예외가 발생한다.")
    @Test
    void executeWithLockWhenNotAcquiredLock() {
        // given
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        LockCallback<String> callback = () -> "callback";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(any(), any(), anyLong(), any())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("락 획득 대기 시간 초과");

        verify(valueOperations, atLeast(2)).setIfAbsent(any(), any(), anyLong(), any());
    }

    @DisplayName("락을 획득하면 콜백이 실행된다.")
    @Test
    void executeWithLockWhenAcquiredLock() throws Throwable {
        // given
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        LockCallback<String> callback = () -> "callback";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(any(), any(), anyLong(), any())).thenReturn(true);

        // when
        String result = lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback);

        // then
        assertThat(result).isEqualTo("callback");
    }

    @DisplayName("락을 획득하지 못하면 재시도를 통해 락을 획득한다.")
    @Test
    void executeWithLockWhenRetry() throws Throwable {
        // given
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        LockCallback<String> callback = () -> "callback";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(any(), any(), anyLong(), any())).thenReturn(false, true);

        // when
        String result = lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback);

        // then
        assertThat(result).isEqualTo("callback");
    }

    @DisplayName("락을 획득하면 콜백이 실행되고, 락을 해제한다.")
    @Test
    void executeWithLockAfterUnlock() throws Throwable {
        // given
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        LockCallback<String> callback = () -> "callback";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(any(), any(), anyLong(), any())).thenReturn(true);

        // when
        lockTemplate.executeWithLock("key", 1L, 1L, TimeUnit.SECONDS, callback);

        // then
        verify(redisTemplate, times(1)).execute(any(DefaultRedisScript.class), eq(Collections.singletonList("key")), any());
    }
}