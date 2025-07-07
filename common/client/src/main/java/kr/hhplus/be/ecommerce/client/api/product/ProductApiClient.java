package kr.hhplus.be.ecommerce.client.api.product;

import kr.hhplus.be.ecommerce.client.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service", url = "${endpoints.product-service.url}")
public interface ProductApiClient {

    @GetMapping("/api/v2/products")
    ApiResponse<ProductResponse.Products> getProducts(
        @RequestParam("pageSize") Long pageSize,
        @RequestParam(value = "cursor", required = false) Long cursor,
        @RequestParam(value = "ids", required = false) List<Long> ids
    );

    @PostMapping("/api/v1/stocks/deduct")
    ApiResponse<Void> deductStock(@RequestBody StockRequest.Deduct request);

    @PostMapping("/api/v1/stocks/restore")
    ApiResponse<Void> restoreStock(@RequestBody StockRequest.Restore request);
}
