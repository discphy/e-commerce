package kr.hhplus.be.ecommerce.domain.message;

import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import lombok.Builder;
import lombok.Getter;

public class MessageCommand {

    @Getter
    public static class Order {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;

        @Builder
        private Order(Long orderId, Long userId, Long userCouponId, long totalPrice, long discountPrice) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
        }

        public static Order of(OrderEvent.Completed event) {
            return Order.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .userCouponId(event.getUserCouponId())
                .totalPrice(event.getTotalPrice())
                .discountPrice(event.getDiscountPrice())
                .build();
        }
    }
}
