package kr.hhplus.be.ecommerce.order.infrastructure;

import kr.hhplus.be.ecommerce.order.domain.Order;
import kr.hhplus.be.ecommerce.order.domain.OrderRepository;
import kr.hhplus.be.ecommerce.order.infrastructure.jpa.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCoreRepository implements OrderRepository {

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
