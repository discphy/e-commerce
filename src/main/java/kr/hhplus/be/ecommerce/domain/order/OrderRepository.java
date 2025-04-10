package kr.hhplus.be.ecommerce.domain.order;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository {

    Order save(Order order);

    Order findById(Long orderId);

    void sendOrderMessage(Order order);

    List<OrderProduct> findOrderIdsIn(List<Long> orderIds);
}
