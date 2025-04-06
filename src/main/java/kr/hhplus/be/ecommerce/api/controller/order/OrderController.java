package kr.hhplus.be.ecommerce.api.controller.order;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.api.controller.ApiResponse;
import kr.hhplus.be.ecommerce.api.controller.order.request.OrderCreateRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @PostMapping("/api/v1/orders")
    public ApiResponse<Void> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        return ApiResponse.success();
    }
}
