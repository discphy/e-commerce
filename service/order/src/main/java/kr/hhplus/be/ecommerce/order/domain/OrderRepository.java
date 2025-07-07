package kr.hhplus.be.ecommerce.order.domain;


public interface OrderRepository {

    Order save(Order order);

    Order findById(Long orderId);

}
