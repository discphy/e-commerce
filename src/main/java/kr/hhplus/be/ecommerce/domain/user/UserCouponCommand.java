package kr.hhplus.be.ecommerce.domain.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponCommand {

    @Getter
    public static class UsableCoupon {

        private final Long userId;
        private final Long couponId;

        private UsableCoupon(Long userId, Long couponId) {
            this.userId = userId;
            this.couponId = couponId;
        }

        public static UsableCoupon of(Long userId, Long couponId) {
            return new UsableCoupon(userId, couponId);
        }
    }

    @Getter
    public static class PublishRequest {

        private final Long userId;
        private final Long couponId;
        private final LocalDateTime issuedAt;

        private PublishRequest(Long userId, Long couponId, LocalDateTime issuedAt) {
            this.userId = userId;
            this.couponId = couponId;
            this.issuedAt = issuedAt;
        }

        public static PublishRequest of(Long userId, Long couponId, LocalDateTime issuedAt) {
            return new PublishRequest(userId, couponId, issuedAt);
        }
    }

    @Getter
    public static class Publish {

        private final Long couponId;
        private final int quantity;
        private final int maxPublishCount;

        private Publish(Long couponId, int quantity, int maxPublishCount) {
            this.couponId = couponId;
            this.quantity = quantity;
            this.maxPublishCount = maxPublishCount;
        }

        public static Publish of(Long couponId, int quantity, int maxPublishCount) {
            return new Publish(couponId, quantity, maxPublishCount);
        }
    }

    @Getter
    public static class PublishFinish {

        private final Long couponId;
        private final int quantity;

        private PublishFinish(Long couponId, int quantity) {
            this.couponId = couponId;
            this.quantity = quantity;
        }

        public static PublishFinish of(Long couponId, int quantity) {
            return new PublishFinish(couponId, quantity);
        }
    }

    @Getter
    public static class Candidates {

        private final Long couponId;
        private final int start;
        private final int end;

        private Candidates(Long couponId, int start, int end) {
            this.couponId = couponId;
            this.start = start;
            this.end = end;
        }

        public static Candidates of(Long couponId, int start, int end) {
            return new Candidates(couponId, start, end);
        }
    }
}
