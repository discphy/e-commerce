package kr.hhplus.be.ecommerce.domain.order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCommand {

    @Getter
    public static class Create {

        private final Long userId;
        private final Long userCouponId;
        private final List<OrderProduct> products;

        @Builder
        private Create(Long userId, Long userCouponId, List<OrderProduct> products) {
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.products = products;
        }

        public static Create of(Long userId, Long userCouponId, List<OrderProduct> products) {
            return Create.builder()
                .userId(userId)
                .userCouponId(userCouponId)
                .products(products)
                .build();
        }
    }

    @Getter
    public static class OrderProduct {

        private final Long productId;
        private final int quantity;

        @Builder
        private OrderProduct(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, int quantity) {
            return OrderProduct.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
        }
    }

    @Getter
    public static class Process {

        private final Long orderId;
        private final OrderProcessTask process;
        private final OrderProcessStatus status;

        @Builder
        private Process(Long orderId, OrderProcessTask process, OrderProcessStatus status) {
            this.orderId = orderId;
            this.process = process;
            this.status = status;
        }

        public static Process ofCouponUsed(Long orderId, OrderProcessStatus status) {
            return Process.builder()
                .orderId(orderId)
                .process(OrderProcessTask.COUPON_USED)
                .status(status)
                .build();
        }

        public static Process ofUsedBalance(Long orderId, OrderProcessStatus status) {
            return Process.builder()
                .orderId(orderId)
                .process(OrderProcessTask.BALANCE_USED)
                .status(status)
                .build();
        }

        public static Process ofStockDeducted(Long orderId, OrderProcessStatus status) {
            return Process.builder()
                .orderId(orderId)
                .process(OrderProcessTask.STOCK_DEDUCTED)
                .status(status)
                .build();
        }
    }
}
