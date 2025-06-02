package kr.hhplus.be.ecommerce.domain.order;

public interface OrderRepository {

    Order save(Order order);

    Order findById(Long orderId);

}
