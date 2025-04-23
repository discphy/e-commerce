package kr.hhplus.be.ecommerce.domain.order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCommand {

    @Getter
    public static class Create {

        private final Long userId;
        private final Long userCouponId;
        private final double discountRate;
        private final List<OrderProduct> products;

        @Builder
        private Create(Long userId, Long userCouponId, double discountRate, List<OrderProduct> products) {
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.discountRate = discountRate;
            this.products = products;
        }

        public static Create of(Long userId, Long userCouponId, double discountRate, List<OrderProduct> products) {
            return Create.builder()
                .userId(userId)
                .userCouponId(userCouponId)
                .discountRate(discountRate)
                .products(products)
                .build();
        }
    }

    @Getter
    public static class OrderProduct {

        private final Long productId;
        private final String productName;
        private final Long productPrice;
        private final int quantity;

        @Builder
        private OrderProduct(Long productId, String productName, Long productPrice, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, String productName, Long productPrice, int quantity) {
            return OrderProduct.builder()
                .productId(productId)
                .productName(productName)
                .productPrice(productPrice)
                .quantity(quantity)
                .build();
        }
    }

    @Getter
    public static class DateQuery {

        private final LocalDate date;

        private DateQuery(LocalDate date) {
            this.date = date;
        }

        public static DateQuery of(LocalDate date) {
            return new DateQuery(date);
        }

        public PaidProducts toPaidProductsQuery(OrderStatus orderStatus) {
            return PaidProducts.of(date, orderStatus);
        }
    }

    @Getter
    public static class PaidProducts {

        private final LocalDate paidAt;
        private final OrderStatus status;

        private PaidProducts(LocalDate paidAt, OrderStatus status) {
            this.paidAt = paidAt;
            this.status = status;
        }

        public static PaidProducts of(LocalDate paidAt, OrderStatus status) {
            return new PaidProducts(paidAt, status);
        }
    }
}
