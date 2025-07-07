package kr.hhplus.be.ecommerce.client.api.product;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductResponse {

    @Getter
    @NoArgsConstructor
    public static class Products {

        private List<Product> products;

        private Products(List<Product> products) {
            this.products = products;
        }

    }

    @Getter
    @NoArgsConstructor
    public static class Product {

        private Long id;
        private String name;
        private long price;
        private int stock;

        @Builder
        private Product(Long id, String name, long price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }
    }

}
