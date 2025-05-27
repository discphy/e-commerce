package kr.hhplus.be.ecommerce.domain.order;

import kr.hhplus.be.ecommerce.support.lock.DistributedLock;
import kr.hhplus.be.ecommerce.support.lock.LockType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderClient orderClient;
    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public OrderInfo.Order createOrder(OrderCommand.Create command) {
        validateUser(command.getUserId());
        List<OrderProduct> products = getProducts(command);
        Optional<OrderInfo.Coupon> coupon = getUsableCoupon(command.getUserCouponId());

        Order order = Order.create(
            command.getUserId(),
            coupon.map(OrderInfo.Coupon::getUserCouponId).orElse(null),
            coupon.map(OrderInfo.Coupon::getDiscountRate).orElse(OrderConstant.NOT_DISCOUNT_RATE),
            products
        );
        orderRepository.save(order);
        orderEventPublisher.created(OrderEvent.Created.of(order));

        return OrderInfo.Order.of(order);
    }

    @Transactional
    public OrderInfo.Completed completedOrder(Long orderId) {
        Order order = orderRepository.findById(orderId);
        order.completed(LocalDateTime.now());

        return OrderInfo.Completed.of(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId);
        order.cancel();
    }

    @Transactional(readOnly = true)
    @DistributedLock(type = LockType.ORDER, key = "#command.orderId")
    public void updateProcess(OrderCommand.Process command) {
        orderRepository.updateProcess(command);
        tryCompletedProcess(command.getOrderId());
    }

    private void validateUser(Long userId) {
        orderClient.getUser(userId);
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

    private void tryCompletedProcess(Long orderId) {
        OrderKey key = OrderKey.of(orderId);
        OrderProcesses processes = OrderProcesses.of(orderRepository.getProcess(key));

        if (processes.existPending()) {
            return;
        }

        Order order = orderRepository.findById(orderId);

        if (processes.existFailed()) {
            orderEventPublisher.failed(OrderEvent.Failed.of(order, processes));
            return;
        }

        orderEventPublisher.paymentWaited(OrderEvent.PaymentWaited.of(order));
    }
}
