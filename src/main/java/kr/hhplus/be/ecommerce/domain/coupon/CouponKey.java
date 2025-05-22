package kr.hhplus.be.ecommerce.domain.coupon;

import kr.hhplus.be.ecommerce.support.key.KeyGeneratable;
import kr.hhplus.be.ecommerce.support.key.KeyType;

import java.util.List;

public class CouponKey implements KeyGeneratable {

    private final Long couponId;

    private CouponKey(Long couponId) {
        this.couponId = couponId;
    }

    public static CouponKey of(Long couponId) {
        return new CouponKey(couponId);
    }

    @Override
    public KeyType type() {
        return KeyType.COUPON;
    }

    @Override
    public List<String> namespaces() {
        return List.of(couponId.toString());
    }
}
