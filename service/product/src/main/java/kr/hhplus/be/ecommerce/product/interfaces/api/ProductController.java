package kr.hhplus.be.ecommerce.product.interfaces.api;

import kr.hhplus.be.ecommerce.product.domain.product.ProductCommand;
import kr.hhplus.be.ecommerce.product.domain.product.ProductInfo;
import kr.hhplus.be.ecommerce.product.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/api/v1/products")
    public ApiResponse<ProductResponse.Products> getProducts() {
        ProductInfo.Products products = productService.getSellingProducts();
        return ApiResponse.success(ProductResponse.Products.of(products));
    }

    @GetMapping("/api/v2/products")
    public ApiResponse<ProductResponse.Products> getProducts(
        @RequestParam("pageSize") Long pageSize,
        @RequestParam(value = "cursor", required = false) Long cursor,
        @RequestParam(value = "ids", required = false) List<Long> ids
    ) {
        ProductInfo.Products products = productService.getProducts(
            ProductCommand.Query.of(pageSize, cursor, ids)
        );
        return ApiResponse.success(ProductResponse.Products.of(products));
    }


}
