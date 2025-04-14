package kr.hhplus.be.ecommerce.domain.order;

import java.util.List;

public interface OrderRepository {

    Order save(Order order);

    Order findById(Long orderId);

    List<OrderProduct> findOrderIdsIn(List<Long> orderIds);
}
