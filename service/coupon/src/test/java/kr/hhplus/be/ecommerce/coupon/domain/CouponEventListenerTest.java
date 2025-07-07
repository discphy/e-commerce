package kr.hhplus.be.ecommerce.coupon.domain;

import kr.hhplus.be.ecommerce.coupon.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CouponEventListenerTest extends MockTestSupport {

    @InjectMocks
    private CouponEventListener couponEventListener;

    @Mock
    private CouponService couponService;

    @DisplayName("쿠폰 발행 이벤트를 수신하여 발행 상태를 갱신한다.")
    @Test
    void handle() {
        // given
        CouponEvent.Published event = new CouponEvent.Published(1L);

        // when
        couponEventListener.handle(event);

        // then
        verify(couponService, times(1)).stopPublishCoupon(event.getId());
    }
}