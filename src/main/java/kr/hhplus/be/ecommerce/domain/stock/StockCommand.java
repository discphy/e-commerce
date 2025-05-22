package kr.hhplus.be.ecommerce.domain.stock;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockCommand {

    @Getter
    public static class Deduct {

        private final List<OrderProduct> products;

        private Deduct(List<OrderProduct> products) {
            this.products = products;
        }

        public static Deduct of(List<OrderProduct> products) {
            return new Deduct(products);
        }
    }

    @Getter
    public static class Restore {

        private final List<OrderProduct> products;

        private Restore(List<OrderProduct> products) {
            this.products = products;
        }

        public static Restore of(List<OrderProduct> products) {
            return new Restore(products);
        }
    }

    @Getter
    public static class OrderProduct {

        private final Long productId;
        private final int quantity;

        private OrderProduct(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, int quantity) {
            return new OrderProduct(productId, quantity);
        }
    }
}
