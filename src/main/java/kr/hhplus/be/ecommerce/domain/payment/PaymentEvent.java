package kr.hhplus.be.ecommerce.domain.payment;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class PaymentEvent {

    @Getter
    public static class Paid {

        private final Long paymentId;
        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final List<OrderProduct> orderProducts;

        @Builder
        private Paid(Long paymentId,
                     Long orderId,
                     Long userId,
                     Long userCouponId,
                     long totalPrice,
                     long discountPrice,
                     List<OrderProduct> orderProducts) {
            this.paymentId = paymentId;
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
            this.orderProducts = orderProducts;
        }
    }

    @Getter
    public static class PayFailed {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final List<OrderProduct> orderProducts;

        @Builder
        private PayFailed(Long orderId,
                          Long userId,
                          Long userCouponId,
                          long totalPrice,
                          long discountPrice,
                          List<OrderProduct> orderProducts) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
            this.orderProducts = orderProducts;
        }
    }

    @Getter
    public static class Canceled {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final List<OrderProduct> orderProducts;

        @Builder
        private Canceled(Long orderId,
                         Long userId,
                         Long userCouponId,
                         long totalPrice,
                         long discountPrice,
                         List<OrderProduct> orderProducts) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
            this.orderProducts = orderProducts;
        }
    }

    @Getter
    public static class OrderProduct {

        private final Long orderProductId;
        private final Long productId;
        private final String productName;
        private final long unitPrice;
        private final int quantity;

        @Builder
        private OrderProduct(Long orderProductId, Long productId, String productName, long unitPrice, int quantity) {
            this.orderProductId = orderProductId;
            this.productId = productId;
            this.productName = productName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }
    }
}
