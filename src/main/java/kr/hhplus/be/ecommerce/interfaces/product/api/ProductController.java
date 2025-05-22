package kr.hhplus.be.ecommerce.interfaces.product.api;

import kr.hhplus.be.ecommerce.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.domain.product.ProductService;
import kr.hhplus.be.ecommerce.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/v1/products")
    public ApiResponse<ProductResponse.Products> getProducts() {
        ProductInfo.Products products = productService.getSellingProducts();
        return ApiResponse.success(ProductResponse.Products.of(products));
    }
}
