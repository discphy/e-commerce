package kr.hhplus.be.ecommerce.domain.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CouponEvent {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PublishRequested {

        private Long userId;
        private Long couponId;

        public static PublishRequested of(Long userId, Long couponId) {
            return PublishRequested.builder()
                .userId(userId)
                .couponId(couponId)
                .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Published {

        private Long id;

        public static Published of(Coupon coupon) {
            return new Published(coupon.getId());
        }
    }
}
