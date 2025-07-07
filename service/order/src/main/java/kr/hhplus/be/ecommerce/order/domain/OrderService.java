package kr.hhplus.be.ecommerce.order.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderClient orderClient;
    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public OrderInfo.Order createOrder(OrderCommand.Create command) {
        List<OrderProduct> products = getProducts(command);
        Optional<OrderInfo.Coupon> coupon = getUsableCoupon(command.getUserCouponId());

        Order order = createOrder(command.getUserId(), coupon, products);
        orderRepository.save(order);

        orderClient.deductStock(command.getProducts());

        orderEventPublisher.created(OrderEvent.Created.of(order));

        return OrderInfo.Order.of(order);
    }

    @Transactional
    public void completedOrder(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId);
            order.completed(LocalDateTime.now());

            orderEventPublisher.completed(OrderEvent.Completed.of(order));
        } catch (Exception e) {
            orderEventPublisher.completeFailed(OrderEvent.CompleteFailed.of(orderId));
            throw e;
        }
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId);

            orderClient.restoreStock(order.getOrderProducts().stream()
                .map(op -> OrderCommand.OrderProduct.of(op.getProductId(), op.getQuantity()))
                .toList());

            order.cancel();
        } catch (Exception e) {
            log.error("주문 취소 실패 - orderId: {}", orderId, e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public OrderInfo.Order getOrder(Long orderId) {
        return OrderInfo.Order.of(orderRepository.findById(orderId));
    }

    private Order createOrder(Long userId, Optional<OrderInfo.Coupon> coupon, List<OrderProduct> products) {
        return Order.create(
            userId,
            coupon.map(OrderInfo.Coupon::getUserCouponId).orElse(null),
            coupon.map(OrderInfo.Coupon::getDiscountRate).orElse(OrderConstant.NOT_DISCOUNT_RATE),
            products
        );
    }

    private List<OrderProduct> getProducts(OrderCommand.Create command) {
        return orderClient.getProducts(command.getProducts()).stream()
            .map(this::createOrderProduct)
            .toList();
    }

    private OrderProduct createOrderProduct(OrderInfo.Product product) {
        return OrderProduct.create(product.getId(), product.getName(), product.getPrice(), product.getQuantity());
    }

    private Optional<OrderInfo.Coupon> getUsableCoupon(Long userCouponId) {
        if (userCouponId == null) {
            return Optional.empty();
        }

        return Optional.of(orderClient.getUsableCoupon(userCouponId));
    }
}
