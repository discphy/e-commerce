package kr.hhplus.be.ecommerce.coupon.infrastructure;

import kr.hhplus.be.ecommerce.client.api.user.UserApiClient;
import kr.hhplus.be.ecommerce.client.api.user.UserResponse;
import kr.hhplus.be.ecommerce.coupon.domain.CouponClient;
import kr.hhplus.be.ecommerce.coupon.domain.CouponInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponCoreClient implements CouponClient {

    private final UserApiClient userApiClient;

    @Override
    public CouponInfo.User getUser(Long userId) {
        UserResponse.User user = userApiClient.getUser(userId).getData();
        return CouponInfo.User.of(user.userId(), user.username());
    }
}
