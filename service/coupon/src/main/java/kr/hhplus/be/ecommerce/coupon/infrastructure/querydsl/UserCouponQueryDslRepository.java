package kr.hhplus.be.ecommerce.coupon.infrastructure.querydsl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.ecommerce.coupon.domain.CouponInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.ecommerce.coupon.domain.QCoupon.coupon;
import static kr.hhplus.be.ecommerce.coupon.domain.QUserCoupon.userCoupon;


@Repository
@RequiredArgsConstructor
public class UserCouponQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<CouponInfo.Coupon> findById(Long userCouponId) {
        return Optional.ofNullable(
            queryFactory.select(
                    Projections.constructor(
                        CouponInfo.Coupon.class,
                        userCoupon.id,
                        userCoupon.couponId,
                        coupon.name,
                        coupon.discountRate,
                        userCoupon.issuedAt
                    )
                )
                .from(userCoupon)
                .innerJoin(coupon).on(userCoupon.couponId.eq(coupon.id))
                .where(userCoupon.id.eq(userCouponId))
                .fetchOne()
        );
    }

    public List<CouponInfo.Coupon> findByUserId(Long userId) {
        return queryFactory.select(
                Projections.constructor(
                    CouponInfo.Coupon.class,
                          userCoupon.id,
                    userCoupon.couponId,
                    coupon.name,
                    coupon.discountRate,
                    userCoupon.issuedAt
                )
            )
            .from(userCoupon)
            .innerJoin(coupon).on(userCoupon.couponId.eq(coupon.id))
            .where(userCoupon.userId.eq(userId))
            .fetch();
    }
}
