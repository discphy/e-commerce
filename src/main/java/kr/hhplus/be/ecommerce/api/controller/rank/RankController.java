package kr.hhplus.be.ecommerce.api.controller.rank;

import kr.hhplus.be.ecommerce.api.controller.ApiResponse;
import kr.hhplus.be.ecommerce.api.controller.product.response.ProductResponse;
import kr.hhplus.be.ecommerce.api.controller.rank.response.RankProductsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RankController {

    @GetMapping("/api/v1/products/ranks")
    public ApiResponse<RankProductsResponse> getRanks() {
        return ApiResponse.success(RankProductsResponse.of(mockProducts()));
    }

    private List<ProductResponse> mockProducts() {
        return List.of(
            ProductResponse.builder()
                .id(1L)
                .name("상품명")
                .price(300000)
                .stock(3)
                .build()
        );
    }
}
