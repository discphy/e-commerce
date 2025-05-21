package kr.hhplus.be.ecommerce.domain.message;

import kr.hhplus.be.ecommerce.domain.order.OrderEvent;
import lombok.Builder;

import java.time.LocalDateTime;

public class MessageCommand {

    public static class Order {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final LocalDateTime paidAt;

        @Builder
        private Order(Long orderId, Long userId, Long userCouponId, long totalPrice, long discountPrice, LocalDateTime paidAt) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
            this.paidAt = paidAt;
        }

        public static Order of(OrderEvent.Paid event) {
            return Order.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .userCouponId(event.getUserCouponId())
                .totalPrice(event.getTotalPrice())
                .discountPrice(event.getDiscountPrice())
                .paidAt(event.getPaidAt())
                .build();
        }
    }
}
