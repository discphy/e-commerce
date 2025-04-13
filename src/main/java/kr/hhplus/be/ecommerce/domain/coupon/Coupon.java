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
public class Coupon {

    @Id
    @Column(name = "coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double discountRate;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private LocalDateTime expiredAt;

    @Builder
    private Coupon(Long id, String name, double discountRate, int quantity, CouponStatus status, LocalDateTime expiredAt) {
        this.id = id;
        this.name = name;
        this.discountRate = discountRate;
        this.quantity = quantity;
        this.status = status;
        this.expiredAt = expiredAt;
    }

    public static Coupon create(String name, double discountRate, int quantity, CouponStatus status, LocalDateTime expiredAt) {
        validateName(name);
        validateDiscountRate(discountRate);
        validateQuantity(quantity);
        validateStatus(status);
        validateExpiredAt(expiredAt);

        return Coupon.builder()
                .name(name)
                .discountRate(discountRate)
                .quantity(quantity)
                .status(status)
                .expiredAt(expiredAt)
                .build();
    }

    public Coupon publish() {
        if (status.cannotPublishable()) {
            throw new IllegalStateException("쿠폰을 발급할 수 없습니다.");
        }

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("쿠폰이 만료되었습니다.");
        }

        if (quantity <= 0) {
            throw new IllegalStateException("쿠폰 수량이 부족합니다.");
        }

        this.quantity--;
        return this;
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("쿠폰 이름은 필수입니다.");
        }
    }

    private static void validateDiscountRate(double discountRate) {
        if (discountRate < 0 || discountRate > 1) {
            throw new IllegalArgumentException("쿠폰 할인율이 올바르지 않습니다.");
        }
    }

    private static void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("쿠폰 수량은 0 이상이어야 합니다.");
        }
    }

    private static void validateStatus(CouponStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("쿠폰 상태는 필수입니다.");
        }
    }

    private static void validateExpiredAt(LocalDateTime expiredAt) {
        if (expiredAt == null || expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("쿠폰 만료일은 현재 시간 이후여야 합니다.");
        }
    }
}
