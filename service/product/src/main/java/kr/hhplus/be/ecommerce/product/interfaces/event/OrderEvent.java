package kr.hhplus.be.ecommerce.product.interfaces.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class OrderEvent {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Created {

        private Long orderId;
        private Long userId;
        private Long userCouponId;
        private long totalPrice;
        private long discountPrice;
        private List<OrderProduct> orderProducts;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Completed {

        private Long orderId;
        private Long userId;
        private Long userCouponId;
        private long totalPrice;
        private long discountPrice;
        private List<OrderProduct> orderProducts;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompleteFailed {

        private Long orderId;

        public static CompleteFailed of(Long orderId) {
            return new CompleteFailed(orderId);
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderProduct {

        private Long orderProductId;
        private Long productId;
        private String productName;
        private long unitPrice;
        private int quantity;
    }
}
