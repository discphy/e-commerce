package kr.hhplus.be.ecommerce.infrastructure.order.repository;

import kr.hhplus.be.ecommerce.domain.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static kr.hhplus.be.ecommerce.domain.order.OrderProcessStatus.PENDING;

@Repository
@RequiredArgsConstructor
public class OrderRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(1);

    public void updateProcess(OrderCommand.Process command) {
        String key = OrderKey.of(command.getOrderId()).generate();

        redisTemplate.opsForHash().put(key, command.getProcess().toLowerCase(), command.getStatus().name());
        redisTemplate.expire(key, DEFAULT_TTL);
    }

    public List<OrderProcess> getProcess(OrderKey key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key.generate());

        if (entries.isEmpty()) {
            return Arrays.stream(OrderProcessTask.values())
                .map(OrderProcess::ofPending)
                .toList();
        }

        return Arrays.stream(OrderProcessTask.values())
                .map(task -> OrderProcess.of(task, getProcessStatus(entries, task)))
                .toList();
    }

    private OrderProcessStatus getProcessStatus(Map<Object, Object> entries, OrderProcessTask task) {
        return Optional.ofNullable(entries.get(task.toLowerCase()))
            .map(value -> OrderProcessStatus.valueOf(value.toString()))
            .orElse(PENDING);
    }
}
