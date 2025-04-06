package kr.hhplus.be.ecommerce.api.controller.product.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProductsResponse {

    private List<ProductResponse> products;

    private ProductsResponse(List<ProductResponse> products) {
        this.products = products;
    }

    public static ProductsResponse of(List<ProductResponse> products) {
        return new ProductsResponse(products);
    }
}
