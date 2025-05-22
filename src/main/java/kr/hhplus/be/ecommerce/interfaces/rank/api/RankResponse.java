package kr.hhplus.be.ecommerce.interfaces.rank.api;

import kr.hhplus.be.ecommerce.domain.rank.RankInfo;
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

        public static PopularProducts of(RankInfo.PopularProducts popularProducts) {
            return new PopularProducts(popularProducts.getProducts().stream()
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

        public static PopularProduct of(RankInfo.PopularProduct product) {
            return PopularProduct.builder()
                .id(product.getProductId())
                .name(product.getProductName())
                .price(product.getProductPrice())
                .build();
        }
    }
}
