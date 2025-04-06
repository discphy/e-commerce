package kr.hhplus.be.ecommerce.api.controller.product;

import kr.hhplus.be.ecommerce.api.controller.ApiResponse;
import kr.hhplus.be.ecommerce.api.controller.product.response.ProductResponse;
import kr.hhplus.be.ecommerce.api.controller.product.response.ProductsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    @GetMapping("/api/v1/products")
    public ApiResponse<ProductsResponse> getProducts() {
        return ApiResponse.success(ProductsResponse.of(mockProducts()));
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
