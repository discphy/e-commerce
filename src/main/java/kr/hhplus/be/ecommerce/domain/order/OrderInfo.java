package kr.hhplus.be.ecommerce.domain.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderInfo {

    @Getter
    public static class Order {

        private final Long orderId;
        private final long totalPrice;
        private final long discountPrice;

        private Order(Long orderId, long totalPrice, long discountPrice) {
            this.orderId = orderId;
            this.totalPrice = totalPrice;
            this.discountPrice = discountPrice;
        }

        public static Order of(Long orderId, long totalPrice, long discountPrice) {
            return new Order(orderId, totalPrice, discountPrice);
        }
    }

    @Getter
    public static class TopPaidProducts {

        private final List<Long> productIds;

        public TopPaidProducts(List<Long> productIds) {
            this.productIds = productIds;
        }

        public static TopPaidProducts of(List<Long> productIds) {
            return new TopPaidProducts(productIds);
        }
    }
}
