package kr.hhplus.be.ecommerce.product.interfaces.api;

import kr.hhplus.be.ecommerce.product.domain.stock.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/api/v1/stocks/deduct")
    public ApiResponse<Void> deductStock(@RequestBody StockRequest.Deduct request) {
        stockService.deductStock(request.toCommand());
        return ApiResponse.success();
    }

    @PostMapping("/api/v1/stocks/restore")
    public ApiResponse<Void> restoreStock(@RequestBody StockRequest.Restore request) {
        stockService.restoreStock(request.toCommand());
        return ApiResponse.success();
    }
}
