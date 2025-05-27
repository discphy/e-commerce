package kr.hhplus.be.ecommerce.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    public OrderInfo.Order createOrder(OrderCommand.Create command) {
        List<OrderProduct> orderProducts = command.getProducts().stream()
            .map(this::createOrderProduct)
            .toList();

        Order order = Order.create(command.getUserId(), command.getUserCouponId(), command.getDiscountRate(), orderProducts);
        orderRepository.save(order);

        return OrderInfo.Order.of(order.getId(), order.getTotalPrice(), order.getDiscountPrice());
    }

    @Transactional
    public void paidOrder(Long orderId) {
        Order order = orderRepository.findById(orderId);
        order.paid(LocalDateTime.now());

        orderEventPublisher.paid(OrderEvent.Paid.of(order));
    }

    private OrderProduct createOrderProduct(OrderCommand.OrderProduct product) {
        return OrderProduct.create(
            product.getProductId(),
            product.getProductName(),
            product.getProductPrice(),
            product.getQuantity()
        );
    }
}
