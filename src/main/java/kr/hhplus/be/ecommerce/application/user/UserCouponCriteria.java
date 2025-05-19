package kr.hhplus.be.ecommerce.application.user;

import kr.hhplus.be.ecommerce.domain.user.UserCouponCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCouponCriteria {

    @Getter
    public static class PublishRequest {

        private final Long userId;
        private final Long couponId;

        private PublishRequest(Long userId, Long couponId) {
            this.userId = userId;
            this.couponId = couponId;
        }

        public static PublishRequest of(Long userId, Long couponId) {
            return new PublishRequest(userId, couponId);
        }

        public UserCouponCommand.PublishRequest toCommand(LocalDateTime dateTime) {
            return UserCouponCommand.PublishRequest.of(userId, couponId, dateTime);
        }
    }

    @Getter
    public static class Publish {

        private final int maxPublishCount;

        private Publish(int maxPublishCount) {
            this.maxPublishCount = maxPublishCount;
        }

        public static Publish of(int maxPublishCount) {
            return new Publish(maxPublishCount);
        }

        public UserCouponCommand.Publish toCommand(Long couponId, int quantity) {
            return UserCouponCommand.Publish.of(couponId, quantity, maxPublishCount);
        }
    }
}
