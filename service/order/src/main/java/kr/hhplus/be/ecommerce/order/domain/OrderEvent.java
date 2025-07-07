package kr.hhplus.be.ecommerce.order.domain;

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

        public static Created of(Order order) {
            return Created.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .userCouponId(order.getUserCouponId())
                .totalPrice(order.getTotalPrice())
                .discountPrice(order.getDiscountPrice())
                .orderProducts(order.getOrderProducts().stream()
                    .map(OrderProduct::of)
                    .toList())
                .build();
        }
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

        public static Completed of(Order order) {
            return Completed.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .userCouponId(order.getUserCouponId())
                .totalPrice(order.getTotalPrice())
                .discountPrice(order.getDiscountPrice())
                .orderProducts(order.getOrderProducts().stream()
                    .map(OrderProduct::of)
                    .toList())
                .build();
        }
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

        public static OrderProduct of(kr.hhplus.be.ecommerce.order.domain.OrderProduct orderProduct) {
            return OrderProduct.builder()
                .orderProductId(orderProduct.getId())
                .productId(orderProduct.getProductId())
                .productName(orderProduct.getProductName())
                .unitPrice(orderProduct.getUnitPrice())
                .quantity(orderProduct.getQuantity())
                .build();
        }
    }
}
