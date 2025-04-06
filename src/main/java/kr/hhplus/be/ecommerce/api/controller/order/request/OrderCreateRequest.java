package kr.hhplus.be.ecommerce.api.controller.order.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderCreateRequest {

    @NotNull(message = "사용자 ID는 필수 입니다.")
    private Long userId;
    private Long couponId;

    @Valid
    @NotEmpty(message = "상품 목록은 1개 이상이여야 합니다.")
    private List<OrderProductRequest> products;

    private OrderCreateRequest(Long userId, Long couponId, List<OrderProductRequest> products) {
        this.userId = userId;
        this.couponId = couponId;
        this.products = products;
    }

    public static OrderCreateRequest of(Long userId, Long couponId, List<OrderProductRequest> products) {
        return new OrderCreateRequest(userId, couponId, products);
    }
}
