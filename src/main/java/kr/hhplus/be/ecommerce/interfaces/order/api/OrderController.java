package kr.hhplus.be.ecommerce.interfaces.order.api;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.domain.order.OrderInfo;
import kr.hhplus.be.ecommerce.domain.order.OrderService;
import kr.hhplus.be.ecommerce.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/api/v1/orders/{orderId}")
    public ApiResponse<OrderResponse.Order> getOrder(@PathVariable("orderId") Long orderId) {
        OrderInfo.Order order = orderService.getOrder(orderId);
        return ApiResponse.success(OrderResponse.Order.of(order));
    }

    @PostMapping("/api/v1/orders")
    public ApiResponse<OrderResponse.Order> orderPayment(@Valid @RequestBody OrderRequest.OrderPayment request) {
        OrderInfo.Order order = orderService.createOrder(request.toCommand());
        return ApiResponse.success(OrderResponse.Order.of(order));
    }
}
