package kr.hhplus.be.ecommerce.order.interfaces.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.ecommerce.order.domain.OrderCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderRequest {

    @Getter
    @NoArgsConstructor
    public static class OrderPayment {

        @NotNull(message = "사용자 ID는 필수 입니다.")
        private Long userId;
        private Long userCouponId;

        @Valid
        @NotEmpty(message = "상품 목록은 1개 이상이여야 합니다.")
        private List<OrderProduct> products;

        private OrderPayment(Long userId, Long userCouponId, List<OrderProduct> products) {
            this.userId = userId;
            this.userCouponId = userCouponId;
            this.products = products;
        }

        public static OrderPayment of(Long userId, Long couponId, List<OrderProduct> products) {
            return new OrderPayment(userId, couponId, products);
        }

        public OrderCommand.Create toCommand() {
            return OrderCommand.Create.of(userId, userCouponId, products.stream()
                .map(r -> OrderCommand.OrderProduct.of(r.getId(), r.getQuantity()))
                .toList()
            );
        }
    }

    @Getter
    @NoArgsConstructor
    public static class OrderProduct {

        @NotNull(message = "상품 ID는 필수입니다.")
        private Long id;

        @NotNull(message = "상품 구매 수량은 필수입니다.")
        @Positive(message = "상품 구매 수량은 양수여야 합니다.")
        private Integer quantity;

        private OrderProduct(Long id, Integer quantity) {
            this.id = id;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long id, Integer quantity) {
            return new OrderProduct(id, quantity);
        }
    }

}
