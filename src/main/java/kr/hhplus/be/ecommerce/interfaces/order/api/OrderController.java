package kr.hhplus.be.ecommerce.interfaces.order.api;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.domain.order.OrderInfo;
import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/v1/orders")
    public ApiResponse<OrderResponse.Order> orderPayment(@Valid @RequestBody OrderRequest.OrderPayment request) {
        OrderInfo.Order order = orderService.createOrder(request.toCommand());
        return ApiResponse.success(OrderResponse.Order.of(order));
    }
}
