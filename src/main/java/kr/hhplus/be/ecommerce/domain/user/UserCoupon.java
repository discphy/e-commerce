package kr.hhplus.be.ecommerce.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon {

    @Id
    @Column(name = "user_coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long couponId;

    @Enumerated(EnumType.STRING)
    private UserCouponUsedStatus usedStatus;

    private LocalDateTime issuedAt;

    private LocalDateTime usedAt;

    @Builder
    private UserCoupon(Long id,
                       Long userId,
                       Long couponId,
                       UserCouponUsedStatus usedStatus,
                       LocalDateTime issuedAt,
                       LocalDateTime usedAt) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.usedStatus = usedStatus;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
    }

    public static UserCoupon create(Long userId, Long couponId) {
        return UserCoupon.builder()
            .userId(userId)
            .couponId(couponId)
            .issuedAt(LocalDateTime.now())
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();
    }

}
