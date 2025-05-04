package kr.hhplus.be.ecommerce.application.rank;

import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankResult {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PopularProducts {

        private List<PopularProduct> products;

        private PopularProducts(List<PopularProduct> products) {
            this.products = products;
        }

        public static PopularProducts of(List<PopularProduct> products) {
            return new PopularProducts(products);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PopularProduct {

        private Long productId;
        private String productName;
        private Long productPrice;

        @Builder
        private PopularProduct(Long productId, String productName, Long productPrice) {
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
        }

        public static PopularProduct of(Long productId, String productName, Long productPrice) {
            return PopularProduct.builder()
                .productId(productId)
                .productName(productName)
                .productPrice(productPrice)
                .build();
        }
    }
}
