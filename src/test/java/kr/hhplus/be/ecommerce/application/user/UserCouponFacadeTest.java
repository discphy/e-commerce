package kr.hhplus.be.ecommerce.application.user;

import kr.hhplus.be.ecommerce.MockTestSupport;
import kr.hhplus.be.ecommerce.domain.coupon.CouponService;
import kr.hhplus.be.ecommerce.domain.user.UserCouponService;
import kr.hhplus.be.ecommerce.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

class UserCouponFacadeTest extends MockTestSupport {

    @InjectMocks
    private UserCouponFacade userCouponFacade;

    @Mock
    private UserService userService;

    @Mock
    private CouponService couponService;

    @Mock
    private UserCouponService userCouponService;

    @DisplayName("사용자 쿠폰을 발급한다.")
    @Test
    void publishUserCoupon() {
        // given
        UserCouponCriteria.Publish criteria = mock(UserCouponCriteria.Publish.class);

        // when
        userCouponFacade.publishUserCoupon(criteria);

        // then
        InOrder inOrder = inOrder(userService, couponService, userCouponService);
        inOrder.verify(userService, times(1)).getUser(criteria.getUserId());
        inOrder.verify(couponService, times(1)).publishCoupon(criteria.getCouponId());
        inOrder.verify(userCouponService, times(1)).createUserCoupon(criteria.toCommand());
    }
}