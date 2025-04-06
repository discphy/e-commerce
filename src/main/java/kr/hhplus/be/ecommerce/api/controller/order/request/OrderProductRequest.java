package kr.hhplus.be.ecommerce.api.controller.order.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderProductRequest {

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long id;

    @NotNull(message = "상품 구매 수량은 필수입니다.")
    @Positive(message = "상품 구매 수량은 양수여야 합니다.")
    private Integer quantity;

    private OrderProductRequest(Long id, Integer quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public static OrderProductRequest of(Long id, Integer quantity) {
        return new OrderProductRequest(id, quantity);
    }
}
