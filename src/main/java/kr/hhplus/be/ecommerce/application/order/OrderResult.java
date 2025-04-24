package kr.hhplus.be.ecommerce.application.order;

import kr.hhplus.be.ecommerce.domain.order.OrderInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResult {

    @Getter
    public static class Order {

        private final Long orderId;
        private final long totalPrice;
        private final long discountPrice;

        private Order(Long orderId, long totalPrice, long discountPrice) {
            this.orderId = orderId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
        }

        public static Order of(OrderInfo.Order order) {
            return new Order(order.getOrderId(), order.getTotalPrice(), order.getDiscountPrice());
        }
    }
}
