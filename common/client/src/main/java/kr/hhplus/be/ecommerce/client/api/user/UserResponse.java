package kr.hhplus.be.ecommerce.client.api.user;

public class UserResponse {

    public record User(
        Long userId,
        String username
    ) {
    }
}
