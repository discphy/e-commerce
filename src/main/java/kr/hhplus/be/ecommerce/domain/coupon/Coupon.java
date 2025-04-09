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
}
