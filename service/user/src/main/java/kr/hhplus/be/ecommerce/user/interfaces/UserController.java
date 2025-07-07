package kr.hhplus.be.ecommerce.user.interfaces;

import kr.hhplus.be.ecommerce.user.domain.UserInfo;
import kr.hhplus.be.ecommerce.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/v1/users/{id}")
    public ApiResponse<UserResponse.User> getUser(@PathVariable("id") Long id) {
        UserInfo.User user = userService.getUser(id);
        return ApiResponse.success(UserResponse.User.of(user));
    }
}
