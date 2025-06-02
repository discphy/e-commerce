package kr.hhplus.be.ecommerce.interfaces.coupon.event;

import kr.hhplus.be.ecommerce.domain.coupon.CouponService;
import kr.hhplus.be.ecommerce.test.support.MockTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CouponMessageEventListenerTest extends MockTestSupport {

    @InjectMocks
    private CouponMessageEventListener couponMessageEventListener;

    @Mock
    private CouponService couponService;

    @DisplayName("쿠폰 발급 요청 이벤트를 수신하면 쿠폰을 발급한다.")
    @Test
    void handle() {
        // given
        String message = """
            {
                "eventId": "12345678-1234-1234-1234-123456789012",
                "eventType": "COUPON_PUBLISH_REQUESTED",
                "payload": {
                    "userId": 1,
                    "couponId": 1
                }
            }
            """;
        Acknowledgment ack = mock(Acknowledgment.class);

        // when
        couponMessageEventListener.handle(message, ack);

        // then
        verify(couponService).publishUserCoupon(any());
        verify(ack).acknowledge();
    }
}