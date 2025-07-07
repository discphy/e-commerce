package kr.hhplus.be.ecommerce.product.domain.rank;

import kr.hhplus.be.ecommerce.product.domain.product.Product;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankInfo {

    @Getter
    public static class ProductScore {

        private final Long productId;
        private final Long totalScore;

        private ProductScore(Long productId, Long totalScore) {
            this.productId = productId;
            this.totalScore = totalScore;
        }

        public static ProductScore of(Long productId, Long totalScore) {
            return new ProductScore(productId, totalScore);
        }
    }

    @Getter
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

        public static PopularProduct of(Product product) {
            return PopularProduct.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .build();
        }
    }
}
