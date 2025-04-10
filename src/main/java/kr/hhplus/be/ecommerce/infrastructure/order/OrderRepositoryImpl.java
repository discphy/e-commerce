package kr.hhplus.be.ecommerce.infrastructure.order;

import kr.hhplus.be.ecommerce.domain.order.Order;
import kr.hhplus.be.ecommerce.domain.order.OrderRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    @Override
    public Order save(Order order) {
        return null;
    }

    @Override
    public Order findById(Long orderId) {
        return null;
    }

    @Override
    public void sendOrderMessage(Order order) {

    }
}
