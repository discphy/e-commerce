package kr.hhplus.be.ecommerce.api.controller.rank.response;

import kr.hhplus.be.ecommerce.api.controller.product.response.ProductResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RankProductsResponse {

    private List<ProductResponse> products;

    private RankProductsResponse(List<ProductResponse> products) {
        this.products = products;
    }

    public static RankProductsResponse of(List<ProductResponse> products) {
        return new RankProductsResponse(products);
    }
}
