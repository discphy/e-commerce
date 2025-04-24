package kr.hhplus.be.ecommerce.application.user;

import kr.hhplus.be.ecommerce.support.MockTestSupport;
import kr.hhplus.be.ecommerce.domain.coupon.CouponInfo;
import kr.hhplus.be.ecommerce.domain.coupon.CouponService;
import kr.hhplus.be.ecommerce.domain.user.UserCouponInfo;
import kr.hhplus.be.ecommerce.domain.user.UserCouponService;
import kr.hhplus.be.ecommerce.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.*;

class UserCouponFacadeUnitTest extends MockTestSupport {

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

    @DisplayName("보유 쿠폰 목록을 조회한다.")
    @Test
    void getUserCoupons() {
        // given
        UserCouponInfo.Coupons coupons = mock(UserCouponInfo.Coupons.class);

        when(coupons.getCoupons())
            .thenReturn(
                List.of(
                    UserCouponInfo.Coupon.builder()
                        .userCouponId(1L)
                        .couponId(1L)
                        .issuedAt(LocalDateTime.of(2025, 4, 1, 12, 0, 0))
                        .build(),
                    UserCouponInfo.Coupon.builder()
                        .userCouponId(2L)
                        .couponId(2L)
                        .issuedAt(LocalDateTime.of(2025, 4, 10, 12, 0, 0))
                        .build()
                )
            );

        when(userCouponService.getUserCoupons(anyLong()))
            .thenReturn(coupons);

        when(couponService.getCoupon(anyLong()))
            .thenReturn(CouponInfo.Coupon.builder()
                .name("10% 쿠폰명")
                .discountRate(0.1)
                .build()
            );

        // when
        UserCouponResult.Coupons result = userCouponFacade.getUserCoupons(1L);

        // then
        InOrder inOrder = inOrder(userCouponService, couponService);
        inOrder.verify(userCouponService, times(1)).getUserCoupons(anyLong());
        inOrder.verify(couponService, times(2)).getCoupon(anyLong());

        assertThat(result.getCoupons())
            .extracting("userCouponId", "couponName", "discountRate")
            .containsExactlyInAnyOrder(
                tuple(1L, "10% 쿠폰명", 0.1),
                tuple(2L, "10% 쿠폰명", 0.1)
            );
    }
}