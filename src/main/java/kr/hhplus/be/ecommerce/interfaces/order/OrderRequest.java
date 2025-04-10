package kr.hhplus.be.ecommerce.interfaces.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.ecommerce.application.order.OrderCriteria;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {

        @NotNull(message = "사용자 ID는 필수 입니다.")
        private Long userId;
        private Long couponId;

        @Valid
        @NotEmpty(message = "상품 목록은 1개 이상이여야 합니다.")
        private List<Product> products;

        private Create(Long userId, Long couponId, List<Product> products) {
            this.userId = userId;
            this.couponId = couponId;
            this.products = products;
        }

        public static Create of(Long userId, Long couponId, List<Product> products) {
            return new Create(userId, couponId, products);
        }

        public OrderCriteria.OrderPayment toCriteria() {
            return OrderCriteria.OrderPayment.of(userId, couponId, products.stream()
                    .map(Product::toCommand)
                    .toList());
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Product {

        @NotNull(message = "상품 ID는 필수입니다.")
        private Long id;

        @NotNull(message = "상품 구매 수량은 필수입니다.")
        @Positive(message = "상품 구매 수량은 양수여야 합니다.")
        private Integer quantity;

        private Product(Long id, Integer quantity) {
            this.id = id;
            this.quantity = quantity;
        }

        public static Product of(Long id, Integer quantity) {
            return new Product(id, quantity);
        }

        public OrderCriteria.OrderProduct toCommand() {
            return OrderCriteria.OrderProduct.of(id, quantity);
        }
    }

}
