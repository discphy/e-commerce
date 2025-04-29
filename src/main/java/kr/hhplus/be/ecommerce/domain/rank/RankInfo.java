package kr.hhplus.be.ecommerce.domain.rank;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RankInfo {

    @Getter
    public static class PopularProduct {

        private final Long productId;
        private final Long totalScore;

        public PopularProduct(Long productId, Long totalScore) {
            this.productId = productId;
            this.totalScore = totalScore;
        }

        public static PopularProduct of(Long productId, Long totalScore) {
            return new PopularProduct(productId, totalScore);
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

        public List<Long> getProductIds() {
            return products.stream()
                .map(PopularProduct::getProductId)
                .toList();
        }
    }
}
