package kr.hhplus.be.ecommerce.domain.coupon;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "user_coupon",
    indexes = {
        @Index(name = "idx_user_status", columnList = "user_id, used_status"),
        @Index(name = "idx_user_coupon", columnList = "user_id, coupon_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_coupon", columnNames = {"user_id", "coupon_id"})
    }
)
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
        return create(userId, couponId, LocalDateTime.now());
    }

    public static UserCoupon create(Long userId, Long couponId, LocalDateTime issuedAt) {
        return UserCoupon.builder()
            .userId(userId)
            .couponId(couponId)
            .issuedAt(issuedAt)
            .usedStatus(UserCouponUsedStatus.UNUSED)
            .build();
    }

    public void use() {
        if (cannotUse()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }

        this.usedStatus = UserCouponUsedStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (!cannotUse()) {
            throw new IllegalStateException("사용할 수 있는 쿠폰을 취소할 수는 없습니다.");
        }

        this.usedStatus = UserCouponUsedStatus.UNUSED;
        this.usedAt = null;
    }

    public boolean cannotUse() {
        return usedStatus.cannotUsable();
    }
}
