package kr.hhplus.be.ecommerce.interfaces.order;

import jakarta.validation.Valid;
import kr.hhplus.be.ecommerce.application.order.OrderFacade;
import kr.hhplus.be.ecommerce.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    @PostMapping("/api/v1/orders")
    public ApiResponse<Void> orderPayment(@Valid @RequestBody OrderRequest.OrderPayment request) {
        orderFacade.orderPayment(request.toCriteria());
        return ApiResponse.success();
    }
}
