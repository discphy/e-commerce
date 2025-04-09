package kr.hhplus.be.ecommerce.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInfo {

    @Getter
    public static class User {

        private final Long userId;
        private final String username;

        @Builder
        private User(Long userId, String username) {
            this.userId = userId;
            this.username = username;
        }
    }
}
