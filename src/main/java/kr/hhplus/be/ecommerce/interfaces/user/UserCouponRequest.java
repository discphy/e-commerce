package kr.hhplus.be.ecommerce.interfaces.user;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.ecommerce.application.user.UserCouponCriteria;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponRequest {

    @Getter
    @NoArgsConstructor
    public static class Publish {

        @NotNull(message = "쿠폰 ID는 필수입니다.")
        private Long couponId;

        private Publish(Long couponId) {
            this.couponId = couponId;
        }

        public static Publish of(Long couponId) {
            return new Publish(couponId);
        }

        public UserCouponCriteria.PublishRequest toCriteria(Long userId) {
            return UserCouponCriteria.PublishRequest.of(userId, couponId);
        }
    }

}
