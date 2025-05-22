package kr.hhplus.be.ecommerce.interfaces.product.api;

import kr.hhplus.be.ecommerce.domain.product.ProductInfo;
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

        public static Products of(ProductInfo.Products products) {
            return new Products(products.getProducts().stream()
                .map(Product::of)
                .toList());
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

        public static Product of(ProductInfo.Product product) {
            return Product.builder()
                .id(product.getProductId())
                .name(product.getProductName())
                .price(product.getProductPrice())
                .stock(product.getQuantity())
                .build();
        }
    }

}
