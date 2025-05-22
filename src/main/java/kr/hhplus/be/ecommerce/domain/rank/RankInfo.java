package kr.hhplus.be.ecommerce.domain.rank;

import kr.hhplus.be.ecommerce.domain.product.Product;
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
    public static class PopularProducts {

        private final List<PopularProduct> products;

        private PopularProducts(List<PopularProduct> products) {
            this.products = products;
        }

        public static PopularProducts of(List<PopularProduct> products) {
            return new PopularProducts(products);
        }
    }

    @Getter
    public static class PopularProduct {

        private final Long productId;
        private final String productName;
        private final Long productPrice;

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
