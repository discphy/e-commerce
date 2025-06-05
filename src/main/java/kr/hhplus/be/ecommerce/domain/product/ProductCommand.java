package kr.hhplus.be.ecommerce.domain.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductCommand {

    @Getter
    public static class OrderProducts {

        private final List<OrderProduct> products;

        private OrderProducts(List<OrderProduct> products) {
            this.products = products;
        }

        public static OrderProducts of(List<OrderProduct> products) {
            return new OrderProducts(products);
        }
    }

    @Getter
    public static class OrderProduct {

        private final Long productId;
        private final int quantity;

        private OrderProduct(Long productId, int quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, int quantity) {
            return new OrderProduct(productId, quantity);
        }
    }

    @Getter
    public static class Products {

        private final List<Long> productIds;

        private Products(List<Long> productIds) {
            this.productIds = productIds;
        }

        public static Products of(List<Long> productIds) {
            return new Products(productIds);
        }
    }

    @Getter
    public static class Query {

        private final Long pageSize;
        private final Long cursor;
        private final ProductSellingStatus status;

        private Query(Long pageSize, Long cursor, ProductSellingStatus status) {
            this.pageSize = pageSize;
            this.cursor = cursor;
            this.status = status;
        }

        public static Query of(Long pageSize, Long cursor) {
            return new Query(pageSize, cursor, ProductSellingStatus.SELLING);
        }
    }
}
