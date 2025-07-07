package kr.hhplus.be.ecommerce.order.interfaces.api;

import kr.hhplus.be.ecommerce.order.domain.OrderInfo;
import kr.hhplus.be.ecommerce.order.domain.OrderStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderResponse {

    @Getter
    public static class Order {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final OrderStatus status;

        private Order(Long orderId, Long userId, Long userCouponId, long totalPrice, long discountPrice, OrderStatus status) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
            this.status = status;
        }

        public static Order of(OrderInfo.Order order) {
            return new Order(
                order.getOrderId(),
                order.getUserId(),
                order.getUserCouponId(),
                order.getTotalPrice(),
                order.getDiscountPrice(),
                order.getStatus()
            );
        }
    }
}
