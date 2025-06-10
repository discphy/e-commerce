package kr.hhplus.be.ecommerce.domain.order;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderInfo {

    @Getter
    public static class Order {

        private final Long orderId;
        private final Long userId;
        private final Long userCouponId;
        private final long totalPrice;
        private final long discountPrice;
        private final OrderStatus status;

        @Builder
        private Order(Long orderId,
                      Long userId,
                      Long userCouponId,
                      long totalPrice,
                      long discountPrice,
                      OrderStatus status) {
            this.orderId = orderId;
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
            this.status = status;
        }

        public static Order of(kr.hhplus.be.ecommerce.domain.order.Order order) {
            return new Order(
                order.getId(),
                order.getUserId(),
                order.getUserCouponId(),
                order.getTotalPrice(),
                order.getDiscountPrice(),
                order.getOrderStatus());
        }
    }

    @Getter
    public static class Product {

        private final Long id;
        private final String name;
        private final long price;
        private final int quantity;

        @Builder
        private Product(Long id, String name, long price, int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        public static Product of(Long id, String name, long price, int quantity) {
            return new Product(id, name, price, quantity);
        }
    }

    @Getter
    public static class Coupon {

        private final Long userCouponId;
        private final Long couponId;
        private final String couponName;
        private final double discountRate;
        private final LocalDateTime issuedAt;

        @Builder
        private Coupon(Long userCouponId, Long couponId, String couponName, double discountRate, LocalDateTime issuedAt) {
            this.userCouponId = userCouponId;
            this.couponId = couponId;
            this.couponName = couponName;
            this.discountRate = discountRate;
            this.issuedAt = issuedAt;
        }

        public static Coupon of(Long userCouponId, Long couponId, String couponName, double discountRate, LocalDateTime issuedAt) {
            return Coupon.builder()
                .userCouponId(userCouponId)
                .couponId(couponId)
                .couponName(couponName)
                .discountRate(discountRate)
                .issuedAt(issuedAt)
                .build();
        }
    }
}
