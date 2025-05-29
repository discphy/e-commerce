package kr.hhplus.be.ecommerce.infrastructure.order.repository;

import kr.hhplus.be.ecommerce.domain.order.Order;
import kr.hhplus.be.ecommerce.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Order findById(Long orderId) {
        return orderJpaRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
    }
}
