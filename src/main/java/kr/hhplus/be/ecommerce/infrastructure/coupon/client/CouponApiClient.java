package kr.hhplus.be.ecommerce.infrastructure.coupon.client;

import kr.hhplus.be.ecommerce.domain.coupon.CouponClient;
import kr.hhplus.be.ecommerce.domain.coupon.CouponInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponApiClient implements CouponClient {

    @Override
    public CouponInfo.User getUser(Long userId) {
        return null;
    }
}
