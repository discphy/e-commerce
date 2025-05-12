package kr.hhplus.be.ecommerce.interfaces.product;

import kr.hhplus.be.ecommerce.application.product.ProductFacade;
import kr.hhplus.be.ecommerce.application.product.ProductResult;
import kr.hhplus.be.ecommerce.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductFacade productFacade;

    @GetMapping("/api/v1/products")
    public ApiResponse<ProductResponse.Products> getProducts() {
        ProductResult.Products products = productFacade.getProducts();
        return ApiResponse.success(ProductResponse.Products.of(products));
    }
}
