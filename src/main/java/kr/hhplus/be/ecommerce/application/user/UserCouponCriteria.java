package kr.hhplus.be.ecommerce.application.user;

import kr.hhplus.be.ecommerce.domain.user.UserCouponCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponCriteria {

    @Getter
    public static class Publish {

        private final Long userId;
        private final Long couponId;

        private Publish(Long userId, Long couponId) {
            this.userId = userId;
            this.couponId = couponId;
        }

        public static Publish of(Long userId, Long couponId) {
            return new Publish(userId, couponId);
        }

        public UserCouponCommand.Publish toCommand() {
            return UserCouponCommand.Publish.of(userId, couponId);
        }
    }
}
