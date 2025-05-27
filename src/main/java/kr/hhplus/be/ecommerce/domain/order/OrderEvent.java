package kr.hhplus.be.ecommerce.domain.order;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class OrderEvent {

    @Getter
    public static class Created {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final List<OrderProduct> orderProducts;

        @Builder
        private Created(Long orderId,
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
    public static class Completed {

        private final Long paymentId;
        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final List<OrderProduct> orderProducts;

        @Builder
        private Completed(Long paymentId,
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
    public static class CompleteFailed {

        private final Long paymentId;
        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final List<OrderProduct> orderProducts;

        @Builder
        private CompleteFailed(Long paymentId,
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
    public static class PaymentWaited {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final List<OrderProduct> orderProducts;

        @Builder
        private PaymentWaited(Long orderId,
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

        public static PaymentWaited of(Order order) {
            return PaymentWaited.builder()
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
    public static class Failed {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final List<OrderProduct> orderProducts;
        private final OrderProcesses processes;

        @Builder
        private Failed(Long orderId,
                       Long userId,
                       Long userCouponId,
                       long totalPrice,
                       long discountPrice,
                       List<OrderProduct> orderProducts,
                       OrderProcesses processes) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
            this.orderProducts = orderProducts;
            this.processes = processes;
        }

        public static Failed of(Order order, OrderProcesses processes) {
            return Failed.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .userCouponId(order.getUserCouponId())
                .totalPrice(order.getTotalPrice())
                .discountPrice(order.getDiscountPrice())
                .orderProducts(order.getOrderProducts().stream()
                    .map(OrderProduct::of)
                    .toList())
                .processes(processes)
                .build();
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

        public static OrderProduct of(kr.hhplus.be.ecommerce.domain.order.OrderProduct orderProduct) {
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
