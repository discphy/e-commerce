package kr.hhplus.be.ecommerce.client.api.order;

import kr.hhplus.be.ecommerce.client.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "${endpoints.order-service.url}")
public interface OrderApiClient {

    @GetMapping("/api/v1/orders/{orderId}")
    ApiResponse<OrderResponse.Order> getOrder(@PathVariable("orderId") Long orderId);
}
