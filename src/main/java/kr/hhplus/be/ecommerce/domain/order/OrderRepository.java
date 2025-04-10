package kr.hhplus.be.ecommerce.domain.order;

import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {

    Order save(Order order);

    Order findById(Long orderId);

    void sendOrderMessage(Order order);
}
