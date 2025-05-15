package kr.hhplus.be.ecommerce.domain.user;

import kr.hhplus.be.ecommerce.support.key.KeyGeneratable;
import kr.hhplus.be.ecommerce.support.key.KeyType;

import java.util.List;

public class UserCouponKey implements KeyGeneratable {

    private final Long couponId;

    private UserCouponKey(Long couponId) {
        this.couponId = couponId;
    }

    public static UserCouponKey of(Long couponId) {
        return new UserCouponKey(couponId);
    }

    @Override
    public KeyType type() {
        return KeyType.USER_COUPON;
    }

    @Override
    public List<String> namespaces() {
        return List.of(couponId.toString());
    }
}
