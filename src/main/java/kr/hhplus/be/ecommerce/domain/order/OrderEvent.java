package kr.hhplus.be.ecommerce.domain.order;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class OrderEvent {

    @Getter
    public static class Paid {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final LocalDateTime paidAt;

        @Builder
        private Paid(Long orderId,
                     Long userId,
                     Long userCouponId,
                     long totalPrice,
                     long discountPrice,
                     LocalDateTime paidAt) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
            this.paidAt = paidAt;
        }

        public static Paid of(Order order) {
            return Paid.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .userCouponId(order.getUserCouponId())
                .totalPrice(order.getTotalPrice())
                .discountPrice(order.getDiscountPrice())
                .paidAt(order.getPaidAt())
                .build();
        }
    }
}
