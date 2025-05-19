package kr.hhplus.be.ecommerce.domain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    public UserCouponInfo.UsableCoupon getUsableCoupon(UserCouponCommand.UsableCoupon command) {
        UserCoupon userCoupon = userCouponRepository.findByUserIdAndCouponId(command.getUserId(), command.getCouponId());

        if (userCoupon.cannotUse()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }

        return UserCouponInfo.UsableCoupon.of(userCoupon.getId());
    }

    public void useUserCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId);
        userCoupon.use();
    }

    public UserCouponInfo.Coupons getUserCoupons(Long userId) {
        List<UserCoupon> coupons = userCouponRepository.findByUserIdAndUsableStatusIn(userId, UserCouponUsedStatus.forUsable());

        return UserCouponInfo.Coupons.of(coupons.stream()
            .map(this::toCouponInfo)
            .toList());
    }

    public boolean requestPublishUserCoupon(UserCouponCommand.PublishRequest command) {
        boolean isSuccess = userCouponRepository.save(command);

        if (!isSuccess) {
            throw new IllegalArgumentException("쿠폰 발급 요청에 실패했습니다.");
        }

        return true;
    }

    public void publishUserCoupons(UserCouponCommand.Publish command) {
        int start = userCouponRepository.countByCouponId(command.getCouponId());
        int end = Math.min(command.getQuantity(), start + command.getMaxPublishCount());

        if (start >= command.getQuantity()) {
            log.info("발급할 쿠폰 수량이 없습니다. 쿠폰 ID : {}", command.getCouponId());
            return;
        }

        List<UserCouponInfo.Candidates> candidates = userCouponRepository
            .findPublishCandidates(UserCouponCommand.Candidates.of(command.getCouponId(), start, end));

        List<UserCoupon> coupons = candidates.stream()
            .map(uc -> UserCoupon.create(uc.getUserId(), command.getCouponId(), uc.getIssuedAt()))
            .toList();

        userCouponRepository.saveAll(coupons);
    }

    public boolean isPublishFinished(UserCouponCommand.PublishFinish command) {
        int publishedCount = userCouponRepository.countByCouponId(command.getCouponId());

        if (publishedCount > command.getQuantity()) {
            log.error("발급된 쿠폰 개수가 발급 가능 개수를 초과했습니다. 쿠폰 ID: {}", command.getCouponId());
        }

        return publishedCount >= command.getQuantity();
    }

    private UserCouponInfo.Coupon toCouponInfo(UserCoupon userCoupon) {
        return UserCouponInfo.Coupon.builder()
            .userCouponId(userCoupon.getId())
            .couponId(userCoupon.getCouponId())
            .issuedAt(userCoupon.getIssuedAt())
            .build();
    }
}
