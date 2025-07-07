package kr.hhplus.be.ecommerce.user.interfaces;

import kr.hhplus.be.ecommerce.user.domain.UserInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {

    @Getter
    @NoArgsConstructor
    public static class User {

        private Long userId;
        private String username;

        private User(Long userId, String username) {
            this.userId = userId;
            this.username = username;
        }

        public static User of(UserInfo.User user) {
            return new User(user.getUserId(), user.getUsername());
        }
    }
}
