package kr.hhplus.be.ecommerce.domain.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderProductInfo {

    @Getter
    public static class TopPaidProducts {

        private final List<Long> productIds;

        public TopPaidProducts(List<Long> productIds) {
            this.productIds = productIds;
        }

        public static TopPaidProducts of(List<Long> productIds) {
            return new TopPaidProducts(productIds);
        }
    }
}
