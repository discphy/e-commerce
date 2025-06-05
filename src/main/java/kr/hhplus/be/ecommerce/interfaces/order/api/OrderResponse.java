package kr.hhplus.be.ecommerce.interfaces.order.api;

import kr.hhplus.be.ecommerce.domain.order.OrderInfo;
import kr.hhplus.be.ecommerce.domain.order.OrderStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResponse {

    @Getter
    public static class Order {

        private final Long orderId;
        private final long totalPrice;
        private final long discountPrice;
        private final OrderStatus status;

        private Order(Long orderId, long totalPrice, long discountPrice, OrderStatus status) {
            this.orderId = orderId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
            this.status = status;
        }

        public static Order of(OrderInfo.Order order) {
            return new Order(order.getOrderId(), order.getTotalPrice(), order.getDiscountPrice(), order.getStatus());
        }
    }
}
