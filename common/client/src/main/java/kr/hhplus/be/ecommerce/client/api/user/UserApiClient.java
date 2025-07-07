package kr.hhplus.be.ecommerce.client.api.user;

import kr.hhplus.be.ecommerce.client.api.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${endpoints.user-service.url}")
public interface UserApiClient {

    @GetMapping("/api/v1/users/{userId}")
    ApiResponse<UserResponse.User> getUser(@PathVariable("userId") Long userId);
}
