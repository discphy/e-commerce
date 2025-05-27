package kr.hhplus.be.ecommerce.infrastructure.order.repository;

import kr.hhplus.be.ecommerce.domain.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderRedisRepository orderRedisRepository;
    private final OrderProductJpaRepository orderProductJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Order findById(Long orderId) {
        return orderJpaRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
    }

    @Override
    public List<OrderProduct> findOrderIdsIn(List<Long> orderIds) {
        return orderProductJpaRepository.findByOrderIdIn(orderIds);
    }

    @Override
    public void updateProcess(OrderCommand.Process command) {
        orderRedisRepository.updateProcess(command);
    }

    @Override
    public List<OrderProcess> getProcess(OrderKey key) {
        return orderRedisRepository.getProcess(key);
    }
}
