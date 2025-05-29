package kr.hhplus.be.ecommerce.interfaces.coupon.api;

import jakarta.validation.constraints.NotNull;
import kr.hhplus.be.ecommerce.domain.coupon.CouponCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponRequest {

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

        public CouponCommand.Publish toCommand(Long id) {
            return CouponCommand.Publish.of(id, couponId);
        }
    }

}
