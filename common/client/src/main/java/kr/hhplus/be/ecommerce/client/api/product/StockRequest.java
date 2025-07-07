package kr.hhplus.be.ecommerce.client.api.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockRequest {

    @Getter
    @NoArgsConstructor
    public static class Deduct {

        private List<Product> products;

        private Deduct(List<Product> products) {
            this.products = products;
        }

        public static Deduct of(List<Product> products) {
            return new Deduct(products);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Restore {

        private List<Product> products;

        private Restore(List<Product> products) {
            this.products = products;
        }

        public static Restore of(List<Product> products) {
            return new Restore(products);
        }

    }

    @Getter
    @NoArgsConstructor
    public static class Product {

        private Long id;

        private Integer quantity;

        private Product(Long id, Integer quantity) {
            this.id = id;
            this.quantity = quantity;
        }

        public static Product of(Long id, Integer quantity) {
            return new Product(id, quantity);
        }
    }
}
