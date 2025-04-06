package kr.hhplus.be.ecommerce.api.controller.product.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private int price;
    private int stock;

    @Builder
    private ProductResponse(Long id, String name, int price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
}
