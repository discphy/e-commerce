package kr.hhplus.be.ecommerce.domain.stock;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class StockEvent {

    @Getter
    public static class Deducted {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final List<OrderProduct> orderProducts;

        @Builder
        private Deducted(Long orderId,
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
    public static class DeductFailed {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;

        @Builder
        private DeductFailed(Long orderId,
                             Long userId,
                             Long userCouponId,
                             long totalPrice,
                             long discountPrice) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
        }
    }

    @Getter
    public static class Restored {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;

        @Builder
        private Restored(Long orderId,
                         Long userId,
                         Long userCouponId,
                         long totalPrice,
                         long discountPrice) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
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
