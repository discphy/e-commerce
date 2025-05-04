package kr.hhplus.be.ecommerce.interfaces.rank;

import kr.hhplus.be.ecommerce.application.rank.RankResult;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankResponse {

    @Getter
    @NoArgsConstructor
    public static class PopularProducts {

        private List<PopularProduct> products;

        private PopularProducts(List<PopularProduct> products) {
            this.products = products;
        }

        public static PopularProducts of(RankResult.PopularProducts products) {
            return new PopularProducts(products.getProducts().stream()
                .map(PopularProduct::of)
                .toList());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class PopularProduct {

        private Long id;
        private String name;
        private long price;

        @Builder
        private PopularProduct(Long id, String name, long price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public static PopularProduct of(RankResult.PopularProduct product) {
            return PopularProduct.builder()
                .id(product.getProductId())
                .name(product.getProductName())
                .price(product.getProductPrice())
                .build();
        }
    }
}
