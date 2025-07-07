package kr.hhplus.be.ecommerce.client.api.balance;

import kr.hhplus.be.ecommerce.client.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "balance-service", url = "${endpoints.balance-service.url}")
public interface BalanceApiClient {

    @PostMapping("/api/v1/users/{id}/balance/use")
    ApiResponse<Void> useBalance(@PathVariable("id") Long id, @RequestBody BalanceRequest.Use request);

    @PostMapping("/api/v1/users/{id}/balance/refund")
    ApiResponse<Void> refundBalance(@PathVariable("id") Long id, @RequestBody BalanceRequest.Refund request);
}
