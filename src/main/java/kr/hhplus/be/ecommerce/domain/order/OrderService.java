package kr.hhplus.be.ecommerce.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderInfo.Order createOrder(OrderCommand.Create command) {
        List<OrderProduct> orderProducts = command.getProducts().stream()
            .map(product -> OrderProduct.create(
                product.getProductId(),
                product.getProductName(),
                product.getProductPrice(),
                product.getQuantity()
            ))
            .toList();

        Order order = Order.create(
            command.getUserId(),
            command.getUserCouponId(),
            command.getDiscountRate(),
            orderProducts);

        orderRepository.save(order);

        return OrderInfo.Order.of(order.getId(), order.getTotalPrice(), order.getDiscountPrice());
    }

    public void paidOrder(Long orderId) {
        Order order = orderRepository.findById(orderId);
        order.paid();
        orderRepository.sendOrderMessage(order);
    }
}
